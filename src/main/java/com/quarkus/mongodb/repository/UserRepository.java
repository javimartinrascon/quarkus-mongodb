package com.quarkus.mongodb.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.InsertOneResult;
import com.quarkus.mongodb.model.Address;
import com.quarkus.mongodb.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class UserRepository {

    @Inject
    MongoClient mongoClient;

    public List<User> list() {
        List<User> list = new ArrayList<>();
        MongoCursor<Document> cursor = getCollection().find().iterator();

        try {
            while (cursor.hasNext()) {
                Document userDocument = cursor.next();

                list.add(User.of(userDocument));
            }
        } finally {
            cursor.close();
        }

        return list;
    }

    public void add(User user) {
        Document userDocument = new Document()
                .append("name", user.getName())
                .append("surname", user.getSurname())
                .append("birthDate", user.getBirthDate())
                .append("address", new Document()
                        .append("name", user.getAddress().getType())
                        .append("streetAddress", user.getAddress().getStreetAddress())
                        .append("city", user.getAddress().getCity())
                        .append("state", user.getAddress().getState())
                        .append("zipCode", user.getAddress().getZipCode()));

        InsertOneResult insertOneResult = getCollection().insertOne(userDocument);
        BsonObjectId objectId = insertOneResult.getInsertedId().asObjectId();
        user.setId(objectId.getValue().toHexString());
    }

    public User findById(String userId) {
        FindIterable<Document> userFindIterable = getCollection().find(eq("_id", new ObjectId(userId)));

        Document userDocument = userFindIterable.first();

        User user = User.of(userDocument);

        return user;
    }


    private MongoCollection getCollection() {
        return mongoClient.getDatabase("users").getCollection("users");
    }
}
