package com.freelancers.Stream24.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.freelancers.Stream24.Entity.Video;
import com.freelancers.Stream24.Service.VideoService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadMedia(@RequestParam("title") String title,
			@RequestParam("description") String description, @RequestParam("video") MultipartFile videoFile,
			@RequestParam("image") MultipartFile imageFile) {
		try {
			Video video = videoService.addMedia(title, description, videoFile, imageFile);
//            return ResponseEntity.ok().body("Upload successful. Video ID: " + video.getId());
			return ResponseEntity.ok().body("Upload successful");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload files: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> getVideo(@PathVariable String id) {
		// Check if the ID is a valid 24-character hexadecimal string
		if (id == null || id.length() != 24 || !id.matches("[0-9a-fA-F]{24}")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID format".getBytes());
		}

		try {
			Optional<byte[]> video = videoService.getVideo(id);
			if (video.isPresent()) {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				return new ResponseEntity<>(video.get(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/videos")
	public ResponseEntity<List<Video>> getMovies() {
		List<Video> movies = videoService.getAllMovies();
		return ResponseEntity.ok(movies);
	}

//    @GetMapping("/{id}")
//    public ResponseEntity<Video> getVideoById(@PathVariable String id) {
//        Video video = videoService.getVideoById(id);
//        if (video != null) {
//            return ResponseEntity.ok(video);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getVideo(@PathVariable String id) {
//        // Check if the ID is a valid 24-character hexadecimal string
//        if (id == null || id.length() != 24 || !id.matches("[0-9a-fA-F]{24}")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID format".getBytes());
//        }
//
//        try {
//            Optional<byte[]> video = videoService.getVideo(id);
//            if (video.isPresent()) {
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                return new ResponseEntity<>(video.get(), headers, HttpStatus.OK);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }  
//  @GetMapping("/{id}/video")
//  public ResponseEntity<?> getVideo(@PathVariable String id) {
//      InputStream videoStream = mediaService.downloadFile(id);
//      return ResponseEntity.ok().body(new InputStreamResource(videoStream));
//  }
//
//  @GetMapping("/{id}/image")
//  public ResponseEntity<?> getImage(@PathVariable String id) {
//      InputStream imageStream = mediaService.downloadFile(id);
//      return ResponseEntity.ok().body(new InputStreamResource(imageStream));
//  }

}
