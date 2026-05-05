# Movie Tracker — Development Branch

## What's been done
- Login and registration with SHA-256 password hashing
- Sidebar navigation: My List, Add Movie/Show, Reports
- Add movies/shows with title, year, type, poster URL
- Delete, star ratings, watched checkbox (UI works, not yet saved to DB)
- Reports screen: total entries, watched count, average rating, highest rated
- Embedded Apache Derby database (temporary — see below)

---

## Why we switched to Derby temporarily

The project originally used Azure MySQL. During development the Azure database credentials were not available, which caused silent connection failures and misleading error messages in the app. To keep building without being blocked, we switched to **Apache Derby** — an embedded database that runs entirely inside the app with no installation, no server, and no credentials needed. It automatically creates a local database folder called `movietrackerdb/` the first time the app runs (this folder is gitignored and will not be committed).

**Derby is only temporary.** Once the Azure credentials are available, follow the steps at the bottom of this file to switch back.

---

## What still needs to be done

### Task 1 — Load movies from the database
Right now the app shows hardcoded fake sample data for every user. This needs to be replaced so each user sees only their own movies when they log in.

**In `MovieDatabase.java` add:**
```java
public List<Movie> getUserMovies(int userId) {
    connectToDatabase();
    List<Movie> movies = new ArrayList<>();
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM movies WHERE user_id = ?")) {
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Movie m = new Movie(
                rs.getString("title"),
                rs.getString("year"),
                rs.getString("posterUrl"),
                rs.getString("type"),
                rs.getInt("rating"),
                rs.getBoolean("watched")
            );
            movies.add(m);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return movies;
}
```

**In `MainController.java` replace:**
```java
loadSampleMovies();
```
**With:**
```java
List<Movie> movies = new MovieDatabase().getUserMovies(MovieTrackerApp.getCurrentUser().getId());
allMovies.addAll(movies);
```

Also update the movies table in `MovieDatabase.java` `connectToDatabase()` to include a `user_id` column:
```java
"CREATE TABLE movies (" +
"id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
"user_id INT NOT NULL," +
"title VARCHAR(200) NOT NULL," +
"year VARCHAR(10)," +
"posterUrl VARCHAR(500)," +
"type VARCHAR(10)," +
"rating INT," +
"watched BOOLEAN" +
")"
```

---

### Task 2 — Persist changes to the database
Star ratings, the watched checkbox, and delete currently only update what you see on screen — nothing is saved to the database. When you restart the app all changes are lost. These need to be wired up to the database.

**In `MovieDatabase.java` add these three methods:**
```java
public void updateMovieRating(int movieId, int rating) {
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "UPDATE movies SET rating = ? WHERE id = ?")) {
        ps.setInt(1, rating);
        ps.setInt(2, movieId);
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

public void updateMovieWatched(int movieId, boolean watched) {
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "UPDATE movies SET watched = ? WHERE id = ?")) {
        ps.setBoolean(1, watched);
        ps.setInt(2, movieId);
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

public void deleteMovie(int movieId) {
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM movies WHERE id = ?")) {
        ps.setInt(1, movieId);
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}
```

**In `Movie.java` add an `id` field** so we can reference the database row:
```java
private int id;
public int getId() { return id; }
public void setId(int id) { this.id = id; }
```

**In `MovieCellController.java` wire up the calls:**
```java
// Star button:
starButton.setOnAction(e -> {
    movie.setRating(rating);
    new MovieDatabase().updateMovieRating(movie.getId(), rating);
    updateItem(movie, false);
});

// Watched checkbox:
watchedCheckBox.setOnAction(e -> {
    movie.setWatched(watchedCheckBox.isSelected());
    new MovieDatabase().updateMovieWatched(movie.getId(), watchedCheckBox.isSelected());
});

// Delete button:
deleteButton.setOnAction(e -> {
    new MovieDatabase().deleteMovie(movie.getId());
    mainController.deleteMovie(movie);
});
```

---

## Switching from Derby back to Azure MySQL

When the Azure credentials are available, make the following changes:

### 1. `pom.xml` — swap the dependency
Remove:
```xml
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
    <version>10.17.1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derbyshared</artifactId>
    <version>10.17.1.0</version>
</dependency>
```
Add:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.3.0</version>
</dependency>
```

### 2. `src/main/java/module-info.java` — swap the module
Remove:
```java
requires org.apache.derby.engine;
requires org.apache.derby.commons;
uses java.sql.Driver;
```
Add:
```java
requires mysql.connector.j;
```

### 3. `src/main/java/org/example/movietracker/MovieDatabase.java` — swap the connection
Change lines 10–12 from:
```java
final static String DB_URL = "jdbc:derby:movietrackerdb;create=true";
final static String USERNAME = "";
final static String PASSWORD = "";
```
To:
```java
final static String DB_URL = "jdbc:mysql://YOUR_AZURE_HOST:3306/YOUR_DATABASE_NAME?useSSL=true&requireSSL=true";
final static String USERNAME = "your_azure_username";
final static String PASSWORD = "your_azure_password";
```
Replace `YOUR_AZURE_HOST`, `YOUR_DATABASE_NAME`, `your_azure_username`, and `your_azure_password` with the real Azure credentials.

### 4. `MovieDatabase.java` — fix the SQL syntax
Derby SQL differs slightly from MySQL. When switching back:

- Change `GENERATED ALWAYS AS IDENTITY` → `AUTO_INCREMENT`
- Change the duplicate key SQLState check from `"23505"` → `"1062"` in `registerUser()`
- Remove the `createTableSafely()` helper and use `CREATE TABLE IF NOT EXISTS` directly

---

## Running the project
```
mvn clean javafx:run
```
The Derby database is created automatically on first run — no setup needed.
