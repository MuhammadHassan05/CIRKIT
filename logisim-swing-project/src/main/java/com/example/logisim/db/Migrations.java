   CREATE TABLE components (
       id INTEGER PRIMARY KEY,
       type TEXT NOT NULL,
       x INTEGER NOT NULL,
       y INTEGER NOT NULL
   );

   CREATE TABLE connections (
       id INTEGER PRIMARY KEY,
       from_id INTEGER,
       to_id INTEGER,
       FOREIGN KEY (from_id) REFERENCES components(id),
       FOREIGN KEY (to_id) REFERENCES components(id)
   );