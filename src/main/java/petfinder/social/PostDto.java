package petfinder.social;

import java.util.Date;
import java.util.List;

public class PostDto {
    private String user_id;
    private String post_id;
    private Date date_posted;
    private List<String> likes;
    private String content;
    private String image;
    
    /**
     * @param user_id
     * @param post_id
     * @param date_posted
     * @param likes
     * @param content
     * @param image
     */
    public PostDto(String user_id, String post_id, Date date_posted, List<String> likes, String content, String image) {
        super();
        this.user_id = user_id;
        this.post_id = post_id;
        this.date_posted = date_posted;
        this.likes = likes;
        this.content = content;
        this.image = image;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the post_id
     */
    public String getPost_id() {
        return post_id;
    }

    /**
     * @param post_id the post_id to set
     */
    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    /**
     * @return the date_posted
     */
    public Date getDate_posted() {
        return date_posted;
    }

    /**
     * @param date_posted the date_posted to set
     */
    public void setDate_posted(Date date_posted) {
        this.date_posted = date_posted;
    }

    /**
     * @return the likes
     */
    public List<String> getLikes() {
        return likes;
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }
}
