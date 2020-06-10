package com.github.shandilyacodes.grpc.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    public static void main(String[] args) {
        System.out.println("Hello I am a gPRC client for Blog");
        BlogClient client = new BlogClient();
        client.run();
    }

    private void run() {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
        Blog blog = Blog.newBuilder()
                .setAuthorId("Ramendu")
                .setTitle("My First Blog")
                .setContent("Hope you like my first blog")
                .build();

        final CreateBlogResponse createBlogResponse = stub.createBlog(CreateBlogRequest.newBuilder().setBlog(blog).build());
        System.out.println("Received the Create Blog Response...");
        System.out.println(createBlogResponse.toBuilder());

        String blogId = createBlogResponse.getBlog().getId();

        final ReadBlogResponse readBlogResponse = stub.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(readBlogResponse.toString());

    }
}