package com.venky.fileexplorer.controller.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {
	@Value("${application.bucket.name}")
	private String bucketName;

	@Autowired
	private AmazonS3 s3client;

	public String uploadFile(MultipartFile file) {
		File fileObj = converMultiPartFileToFile(file);
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		s3client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		fileObj.delete();
		return "File Uploaded" + fileName;
	}

	public byte[] downloadFile(String fileName) {
		com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		try {
			byte[] content = com.amazonaws.util.IOUtils.toByteArray(inputStream);
			return content;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	
	public String deleteFile(String fileName) {
		s3client.deleteObject(bucketName,fileName);
		return fileName +"removed....";
	}

	private File converMultiPartFileToFile(MultipartFile file) {

		File convertedFile = new File(file.getOriginalFilename());
		try {
			FileOutputStream fos = new FileOutputStream(convertedFile);
			fos.write(file.getBytes());
		} catch (Exception e) {
			// TODO: handle exception

		}
		return convertedFile;
	}

}
