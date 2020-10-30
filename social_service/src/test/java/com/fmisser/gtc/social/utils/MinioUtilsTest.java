package com.fmisser.gtc.social.utils;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MinioUtilsTest {

    @BeforeAll
    static void init() {
        MinioUtils.init();
    }

    @AfterAll
    static void destroy() {

    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void bucketExists() {
        String bucketName = "test-bucket";
        boolean exist = MinioUtils.bucketExists(bucketName);
        Assertions.assertTrue(exist);
    }

    @Test
    void createBucket() {
        String newBucketName = "test-bucket-2";
        Assertions.assertFalse(MinioUtils.bucketExists(newBucketName));
        MinioUtils.createBucket(newBucketName);
        Assertions.assertTrue(MinioUtils.bucketExists(newBucketName));
    }

    @Test
    void getAllBuckets() {
    }

    @Test
    void upload() {
    }

    @Test
    void put() {
    }

    @Test
    void delete() {
    }

    @Test
    void getObjectUrl() {
        String bucketName = "test-bucket";
        String objectName = "images/tp1.png";
        String returnUrl = MinioUtils.getObjectUrl(bucketName, objectName);
        System.out.println("object url is:" + returnUrl);
        Assertions.assertNotNull(returnUrl);
    }
}