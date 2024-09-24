package com.freelancers.Stream24.Service;

import java.io.IOException;
import java.io.InputStream;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@Service
public class MediaService {
	 @Autowired
	    private GridFSBucket gridFSBucket;

	    public ObjectId storeFile(MultipartFile file) throws IOException {
	        try (InputStream inputStream = file.getInputStream()) {
	            GridFSUploadOptions options = new GridFSUploadOptions()
	                    .chunkSizeBytes(358400); // Optional: Adjust chunk size as needed

	            return gridFSBucket.uploadFromStream(file.getOriginalFilename(), inputStream, options);
	        }
	    }

	    public InputStream downloadFile(String id) {
	        return gridFSBucket.openDownloadStream(new ObjectId(id));
	    }

}

