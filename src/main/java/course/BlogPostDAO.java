package course;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

// Possible exercises to play with:
//  find post by author
//  find comments by name
//  find post by tag


public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
//        System.out.println("Got posts collection ");
        postsCollection = blogDatabase.getCollection("posts");
//        System.out.println(postsCollection.count());
//        System.out.println(blogDatabase.getCollection("users").count());
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // XXX HW 3.2,  Work Here
        Document post = null;

        MongoCursor<Document> cursor=postsCollection.find(new BasicDBObject("permalink", permalink)).iterator();
        post=cursor.next();

        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection

        // Want to get ALL the documents, so don't really need a filter.  Add a sort to get the posts in order
        List<Document> posts = new LinkedList<Document>();;
        Document theSort=new Document("date",-1);
//        Document theFltr=new Document("author","bob");

        // Empty filter and sort to get the first relevant post (and we can then step through the list using iterator)
        MongoCursor<Document> cursor=postsCollection.find().sort(theSort).limit(limit).iterator();

        // Step through and to our list (makes little odds if it's array or linked list)
        while(cursor.hasNext()) {
            posts.add(cursor.next());
        }

        // pass list back
        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        // XXX HW 3.2, Work Here
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date, title
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        // Build the post object and insert it
        Document post = new Document();

        Date now = new Date();

        // Comments will be in the form of an array of individual documents
        // Create an EMPTY array in readiness for the future
        List comments = new ArrayList<Object>();

        // Create new post by bundling the various key/value pairs that are needed
        post.append("author", username)
            .append("title", title)
            .append("body", body)
            .append("permalink", permalink)
            .append("tags", tags)
            .append("date", now)
            .append("comments", comments);

        // Insert the freshly minted document in to collection
        postsCollection.insertOne(post);

        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments

//        DBObject comment = new BasicDBObject()
        Document comment = new Document()
                .append("author", name)
                .append("email",  email)
                .append("body",   body);

        // Need to identify the post that the comments will be added in to
        Document thePost =new Document("permalink", permalink);

        // Specify the update.  Push in a "comment" document in to the "comments" array in the post
        Document theUpdt =new Document("$push", new Document("comments",comment));

        // Specify the post to change and how to change it
        postsCollection.updateOne(thePost,theUpdt);

    }
}
