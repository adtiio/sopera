package com.sopera.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sopera.config.JwtUtil;
import com.sopera.model.Brand;
import com.sopera.model.Creator;
import com.sopera.model.User;
import com.sopera.model.YoutubeAccount;
import com.sopera.model.enums.SocialMediaPlatform;
import com.sopera.model.request.AddPlatformRequest;
import com.sopera.model.request.UpdateRequest;
import com.sopera.repository.BrandRepository;
import com.sopera.repository.CreatorRepository;
import com.sopera.repository.UserRepository;
import com.sopera.repository.YoutubeAccountRepository;
import com.sopera.service.YoutubeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CreatorRepository creatorRepository;
    private final YoutubeAccountRepository youtubeRepository;
    private final YoutubeService youtubeService;
    private final BrandRepository brandRepository;
    
    @GetMapping("/details")
    public User getUserDetails(@RequestHeader("Authorization") String jwt){
        String email= jwtUtil.extractUsername(jwt);
        return userRepository.findByEmail(email).get();
    }
    
    @PostMapping("/setRole")
    public ResponseEntity<?> setRole(@RequestHeader("Authorization") String jwt, @RequestBody UpdateRequest role){
        
        String email=jwtUtil.extractUsername(jwt);

        User user=userRepository.findByEmail(email).get();
        System.out.println("hello");
       

        if(role.getData().toLowerCase().equals("creator")){
            user.setRole("CREATOR");

            Creator creator;
            if(user.getUserRole()==null){
                creator=new Creator();
            }else{
                creator=(Creator) user.getUserRole();
            }
            creator.setUser(user);
            
            userRepository.save(user);
            creatorRepository.save(creator);
            return ResponseEntity.ok(creator);
        }else if(role.getData().toLowerCase().equals("brand")){
            user.setRole("BRAND");
            Brand brand;
            if(user.getUserRole()==null || (user.getRole()!=null && user.getRole().equals("CREATOR"))){
                brand=new Brand();
            }else{
                brand=(Brand) user.getUserRole();
            }
            brand.setUser(user);
            userRepository.save(user);
            brandRepository.save(brand);
            return ResponseEntity.ok(brand);
            
        }
        
        return new ResponseEntity<>(role,HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/addPlatform")
    public ResponseEntity<?> addSocialMediaAccount(@RequestHeader("Authorization") String jwt ,@RequestBody AddPlatformRequest info){

        String email=jwtUtil.extractUsername(jwt);
        User user=userRepository.findByEmail(email).get();

        if(info.getPlatformType().equals("youtube")){
            Map<String,Object> response=youtubeProcess(info, user, email);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(Map.of("error", "wrong platform name"));

    }

    @PostMapping("/addCategories")
    public ResponseEntity<?> addCategories(@RequestHeader("Authorization") String jwt, @RequestBody List<String> categories){

        String email=jwtUtil.extractUsername(jwt);
        User user=userRepository.findByEmail(email).get();

        if(user.getUserRole()!=null){
            Creator creator = (Creator) user.getUserRole();
            if(creator.getCategories()==null){
                System.out.println("Adding list");
                creator.setCategories(new ArrayList<>());
            }
            
            for(String category : categories){
                creator.getCategories().add(category);
            }
            Creator savedCreator = creatorRepository.save(creator);
            return ResponseEntity.ok(savedCreator);
        }
        return ResponseEntity.ok("Please select role first");

    }

    @PostMapping("/addBio")
    public ResponseEntity<?> addBio(@RequestHeader("Authorization") String jwt, @RequestBody UpdateRequest bio){

        String email=jwtUtil.extractUsername(jwt);

        User user=userRepository.findByEmail(email).get();

        if(user.getUserRole()==null){
            return new ResponseEntity<>("Please set role first" , HttpStatus.BAD_REQUEST);
        }
        Creator creator=(Creator) user.getUserRole();
        creator.setBio(bio.getData());
        
        return ResponseEntity.ok(creatorRepository.save(creator));

    }

    @GetMapping("/findByCategory")
    public ResponseEntity<List<Creator>> findByCategory(@RequestParam String category) {
        System.out.println("Category: "+category);
        List<Creator> creators = creatorRepository.findByCategory(category);
        return ResponseEntity.ok(creators);
    }


    private Map<String,Object> youtubeProcess(AddPlatformRequest info, User user, String email){
        
        String username=info.getUsername();
        Creator creator=(Creator) user.getUserRole();

        try{
            Map<String,Object> data= youtubeService.getChannelDetails(username);

            YoutubeAccount youtubeAccount=new YoutubeAccount();
            youtubeAccount.setUsername(username);
            youtubeAccount.setPlatform(SocialMediaPlatform.YOUTUBE);
            youtubeAccount.setCreator(creator);
            youtubeAccount.setSubscribers(Integer.parseInt((data.get("subscribers")+"").trim()));
            youtubeAccount.setTotalVideos(Integer.parseInt((data.get("totalVideo")+"").trim()));
            youtubeAccount.setTotalViews(Long.parseLong((data.get("totalViews")+"").trim()));

            creator.getSocialMediaAccounts().add(youtubeAccount);
            youtubeRepository.save(youtubeAccount);
            creatorRepository.save(creator);

            return Map.of(
                "creator", creator,
                "youtubeAccount", youtubeAccount,
                "creator-name" , user.getEmail()
            );

            
        }catch(Exception e){
            return Map.of("error", "Failed to fetch video stats: " + e.getMessage());
        }
    }
}

