/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AnEmptyController {
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	//Video video = new Video(null, null, 0, 0);
	@Autowired
	private VideoRepository videoRepository;

	@RequestMapping(value = "/video", method = RequestMethod.POST, produces = "application/json")
	public Video addVideo(@RequestBody Video video){		
		videoRepository.save(video);
		return video;
	}

	@RequestMapping(value = "/video", method = RequestMethod.GET, produces = "application/json")
	public Iterable<Video> getVideos(){				
		return videoRepository.findAll();
	}

	
	@RequestMapping(value = "/video/{id}", method = RequestMethod.GET, produces = "application/json")
	public Video getVideoById(@PathVariable("id") Long id  ,HttpServletResponse response){						
		
		Optional<Video> requestedVideo = videoRepository.findById(id);
		if ( requestedVideo.isPresent() ){
			return requestedVideo.get();
		}else{
			response.setStatus(404);
			return null;			
		}
	}

	@RequestMapping(value = "/video/{id}/like", method = RequestMethod.POST, produces = "application/json")
	public void addLikeToVideo(@PathVariable("id") Long id, Authentication authentication ,HttpServletResponse response){						
		Optional<Video> requestedVideo = videoRepository.findById(id); 
		if ( requestedVideo.isPresent() ){
			// if video exists		
			Video video = requestedVideo.get(); // copy of video
			Set<String> usersWhoLikedVideo = video.getLikedBy();
			if ( usersWhoLikedVideo.contains( authentication.getName() ) ){				
				// if video is already liked by user
				response.setStatus(400);
			}
			else {
				// if video has not been liked by user
				Set<String> newUsersWhoLikedVideo = usersWhoLikedVideo;
				Long newLikes = video.getLikes();
				newUsersWhoLikedVideo.add(authentication.getName());
				newLikes += 1;
				video.setLikedBy(newUsersWhoLikedVideo); 
				video.setLikes(newLikes);								
				videoRepository.save(video); // updates the video obj
			}
		}else{
			// if video does not exists 
			response.setStatus(404);			
		}
	}

	@RequestMapping(value = "/video/{id}/unlike", method = RequestMethod.POST, produces = "application/json")
	public void unlikeVideo(@PathVariable("id") Long id, Authentication authentication ,HttpServletResponse response){
		Optional<Video> requestedVideo = videoRepository.findById(id); 
		if ( requestedVideo.isPresent() ){
			// if video exists		
			Video video = requestedVideo.get(); // copy of video
			Set<String> usersWhoLikedVideo = video.getLikedBy();
			if ( !usersWhoLikedVideo.contains( authentication.getName() ) ){				
				// if video is already liked by user
				response.setStatus(400);
			}
			else {
				// if video has been liked by user
				Set<String> newUsersWhoLikedVideo = usersWhoLikedVideo;
				Long newLikes = video.getLikes();				
				newUsersWhoLikedVideo.remove(authentication.getName());
				newLikes -= 1;
				video.setLikedBy(newUsersWhoLikedVideo); 
				video.setLikes(newLikes);
				videoRepository.save(video); // updates the video obj
			}
		}else{
			// if video does not exists 
			response.setStatus(404);			
		}
	}

	@RequestMapping(value = "/video/search/findByName", method = RequestMethod.GET, produces = "application/json")
	public Iterable<Video> findVideoByName(@RequestParam("title") String title){		
		return videoRepository.findByName(title);
	}

	@RequestMapping(value = "/video/search/findByDurationLessThan", method = RequestMethod.GET, produces = "application/json")
	public Iterable<Video> findVideoByDurationLessThan(@RequestParam("duration") Long duration){		
		return videoRepository.findByDurationLessThan(duration);
		//return videoRepository.findByName("hello");
	}
	
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}
	
}
