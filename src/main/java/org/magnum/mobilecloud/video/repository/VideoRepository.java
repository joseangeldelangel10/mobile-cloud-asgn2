package org.magnum.mobilecloud.video.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long>{
    
    public static void hello(){        
        System.out.println("hello");
    }

    public List<Video> findByName(String title);

    //@Query(SELECT v FROM Video WHERE v.duration < duration)
    public List<Video> findByDurationLessThan(Long duration);
}
