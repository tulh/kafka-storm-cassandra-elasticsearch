import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;

import javax.persistence.*;
import java.util.*;

/**
 * Created by tulh on 14/07/2016.
 */
@Entity
@Table(name = "blog_posts", schema = "KunderaExamples@secIdxCassandraTest")
@IndexCollection(columns = {@Index(name = "body")})
public class BlogPost
{
    @Id
    @Column(name = "post_id")
    private int postId;

    //Body of the post
    @Column(name = "body")
    private String body;

    //Useful tags specified by author
    @ElementCollection
    @Column(name = "tags")
    private Set<String> tags;

    //List of user IDs who liked this blog post
    @ElementCollection
    @Column(name = "liked_by")
    private List<Integer> likedBy;

    //User IDs and their respective comments on this blog
    @ElementCollection
    @Column(name = "comments")
    private Map<Integer, String> comments;

    /**
     * @return the postId
     */
    public int getPostId()
    {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(int postId)
    {
        this.postId = postId;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * @return the tags
     */
    public Set<String> getTags()
    {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }

    /**
     * @return the likedBy
     */
    public List<Integer> getLikedBy()
    {
        return likedBy;
    }

    /**
     * @param likedBy the likedBy to set
     */
    public void setLikedBy(List<Integer> likedBy)
    {
        this.likedBy = likedBy;
    }

    /**
     * @return the comments
     */
    public Map<Integer, String> getComments()
    {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(Map<Integer, String> comments)
    {
        this.comments = comments;
    }

    public void addTag(String tag)
    {
        if (tags == null)
        {
            tags = new HashSet<String>();
        }
        tags.add(tag);
    }

    public void addLikedBy(int likedByUserId)
    {
        if (likedBy == null)
        {
            likedBy = new ArrayList<Integer>();
        }
        likedBy.add(likedByUserId);
    }

    public void addComment(int userId, String comment)
    {
        if (comments == null)
        {
            comments = new HashMap<Integer, String>();
        }
        comments.put(userId, comment);
    }
}