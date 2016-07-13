import com.datastax.driver.core.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by tulh on 08/07/2016.
 */
public class CassandraClient
{
    private final static String USERNAME = "cassandra";
    private final static String PASSWORD = "cassandra";
    private Cluster cluster;
    private Session session;

    public void connect(String node)
    {
        cluster = Cluster.builder()
                .addContactPoint(node)
                .withCredentials(USERNAME, PASSWORD)
                .build();
        session = cluster.connect();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts())
        {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
    }

    public void createSchema()
    {
        session.execute("create keyspace simplex with replication " +
                "={'class':'SimpleStrategy', 'replication_factor':3};");
        session.execute("create table simplex.songs (" +
                "id uuid primary key," +
                "title text," +
                "album text," +
                "artist text," +
                "tags set<text>," +
                "data blob" +
                ");");
        session.execute("create table simplex.playlists (" +
                "id uuid," +
                "title text," +
                "album text," +
                "artist text," +
                "song_id uuid," +
                "primary key (id, title, album, artist)" +
                ");");
    }

    public void loadData()
    {
        session.execute(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'," +
                        "{'jazz', '2013'})" +
                        ";");
        session.execute(
                "INSERT INTO simplex.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'" +
                        ");");
        session.execute(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "126716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'Rock'," +
                        "'Bye Bye Beautiful'," +
                        "'NightWish'," +
                        "{'Rock', '2013'})" +
                        ";");
        session.execute(
                "INSERT INTO simplex.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "12c9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "126716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'Rock'," +
                        "'Bye Bye Beautiful'," +
                        "'NightWish'" +
                        ");");
    }

    public void deleteData()
    {
        session.execute("delete from simplex.songs where id=126716f7-2e54-4715-9f00-91dcbea6cf50;");
    }

    public void boundStatement()
    {
        PreparedStatement statement = session.prepare(
                "INSERT INTO simplex.songs " +
                        "(id, title, album, artist, tags) " +
                        "VALUES (?, ?, ?, ?, ?);");
        BoundStatement boundStatement = new BoundStatement(statement);
        Set<String> tags = new HashSet<String>();
        tags.add("jazz");
        tags.add("2013");
        session.execute(boundStatement.bind(
                UUID.fromString("756716f7-2e54-4715-9f00-91dcbea6cf50"),
                "La Petite Tonkinoise'",
                "Bye Bye Blackbird'",
                "Joséphine Baker",
                tags ) );

        statement = session.prepare(
                "INSERT INTO simplex.playlists " +
                        "(id, song_id, title, album, artist) " +
                        "VALUES (?, ?, ?, ?, ?);");
        boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind(
                UUID.fromString("2cc9ccb7-6221-4ccb-8387-f22b6a1b354d"),
                UUID.fromString("756716f7-2e54-4715-9f00-91dcbea6cf50"),
                "La Petite Tonkinoise",
                "Bye Bye Blackbird",
                "Joséphine Baker") );
    }

    public void querySchema()
    {
        ResultSet results = session.execute("SELECT * FROM simplex.playlists; ");
        System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title", "album", "artist",
                "-------------------------------+-----------------------+--------------------"));
        for (Row row : results) {
            System.out.println(String.format("%-30s\t%-20s\t%-20s", row.getString("title"),
                    row.getString("album"),  row.getString("artist")));
        }
        System.out.println();
        results = session.execute("SELECT * FROM simplex.songs;");
        System.out.println(String.format("%-30s\t%-20s\t%-20s\t%s\n%s", "title", "album", "artist", "tags",
                "-------------------------------+-----------------------+--------------------+-------------------"));
        for (Row row : results) {
            System.out.println(String.format("%-30s\t%-20s\t%-20s\t%s", row.getString("title"),
                    row.getString("album"),  row.getString("artist"), row.getSet("tags", String.class).toString()));
        }
        System.out.println();
    }

    public void close()
    {
        session.close();
        cluster.close();
    }

    public static void main(String args[])
    {
        CassandraClient client = new CassandraClient();
        client.connect("172.16.10.124");
//        client.createSchema();
        client.loadData();
        client.boundStatement();
        client.querySchema();
        client.deleteData();
        client.close();

        client = new CassandraClient();
        client.connect("172.16.10.123");
        client.querySchema();
        client.close();


    }

}
