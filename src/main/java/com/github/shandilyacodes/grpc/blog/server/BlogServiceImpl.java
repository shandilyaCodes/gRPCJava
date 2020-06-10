package com.github.shandilyacodes.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("myDB");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        System.out.println("Received Create Blog Request!");
        Blog blog = request.getBlog();
        Document document = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        System.out.println("Inserting Blog...");
        // Insert (Create) document in mongodb
        collection.insertOne(document);

        // Retrieve the mongodb generated ID
        String id = document.getObjectId("_id").toString();
        System.out.println("Inserted Blog : " + id);
        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder()
                        .setId(id)
                        .build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("Received Read Blog Request!");
        String blogId = request.getBlogId();
        System.out.println("Searching for a blog...");
        final Document result = collection.find(Filters.eq("_id", new ObjectId(blogId))).first();

        if(result == null) {
            System.out.println("Blog not Found!");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The Blog with given ID not found!")
                            .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, sending response!");
            Blog blog = Blog.newBuilder()
                    .setId(blogId)
                    .setAuthorId(result.getString("author_id"))
                    .setContent(result.getString("content"))
                    .setTitle(result.getString("title"))
                    .build();
            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {

    }
}