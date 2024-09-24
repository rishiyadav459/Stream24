package com.freelancers.Stream24.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.freelancers.Stream24.Entity.Video;
import com.freelancers.Stream24.Repository.VideoRepository;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;

@Service
public class VideoService {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private MongoDatabaseFactory mongoDatabaseFactory;

	@Autowired
	private VideoRepository videoRepository;

	public Video addMedia(String title, String description, MultipartFile videoFile, MultipartFile imageFile)
			throws IOException {
		// Store files in GridFS
		String videoId = mediaService.storeFile(videoFile).toHexString();
		String imageId = mediaService.storeFile(imageFile).toHexString();

		// Create Video document with metadata and GridFS IDs
		Video video = new Video();
		video.setTitle(title);
		video.setDescription(description);
		video.setVideoId(videoId);
		video.setImageId(imageId);

		return videoRepository.save(video);
	}

	public Optional<byte[]> getVideo(String id) throws IOException {
		GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
		GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(new ObjectId(id));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int data;
		byte[] buffer = new byte[1024];
		while ((data = downloadStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, data);
		}
		return Optional.of(outputStream.toByteArray());
	}

	public List<Video> getAllMovies() {
		return videoRepository.findAll();
	}

}
