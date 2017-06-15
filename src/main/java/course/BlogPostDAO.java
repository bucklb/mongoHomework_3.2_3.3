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

        //        post = postsCollection.findOne(new BasicDBObject("permalink", permalink));


        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection

        List<Document> posts = null;

        /*
        List<Document> posts = new ArrayList<Document>();

        MongoCursor<Document> cursor=postsCollection.find().iterator();
        try{

            // Step through the collection and add it to posts collection
            while (cursor.hasNext()) {

                System.out.println("looking for next in cursor ...");
                posts.add(cursor.next());
            }

            System.out.println(posts.size());
            System.out.println(posts);

        }finally{
            // let it go, let it go ...
            cursor.close();
        }
        return posts;
*/


/*
        posts = new ArrayList<Document>();

        List<Document> list = postsCollection.find().sort(new Document().append("date", -1)).limit(limit).into(new ArrayList<Document>());

        for (Document document : list) {
            posts.add(document);
        }
*/




        MongoCursor<Document> cursor=postsCollection.find().sort(new BasicDBObject().append("date", -1)).limit(limit).iterator();
//        MongoCursor<Document> cursor=postsCollection.find().iterator();
        posts = new ArrayList<Document>();

        while(cursor.hasNext()) {
            //System.out.println(cursor.next());
            Document d = cursor.next();
            System.out.println(d);
            posts.add(d);
        }

        System.out.println(posts);


/*
        MongoCursor<DBObject> cursor=postsCollection.find().iterator();
        posts = new LinkedList<DBObject>();
        for (DBObject value : cursor) {
            posts.add(value);
        }
        System.out.println("Value from the DB" + posts);
*/




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
        List comments = new ArrayList<Object>();
        post.append("author", username)
            .append("title", title)
            .append("body", body)
            .append("permalink", permalink)
            .append("tags", tags)
            .append("date", now)
            .append("comments", comments);
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

        DBObject comment = new BasicDBObject()
                .append("author", name)
                .append("email", email)
                .append("body", body);

        postsCollection.updateOne(new  BasicDBObject("permalink", permalink),
                new BasicDBObject("$push", new BasicDBObject("comments", comment)));

    }
}
