CREATE TABLE "User"(
  "userId" TEXT,
  "email" TEXT,
  "name" TEXT,
  PRIMARY KEY ("userId")
);

CREATE TABLE "Party"(
  "partyId" INTEGER,
  "spotifyPlaylistId" TEXT,
  "name" TEXT,
  "lat" REAL,
  "lon" REAL,
  "time" TEXT,
  "status" TEXT,
  PRIMARY KEY ("partyId")
);

CREATE TABLE "SongRequest"(
  "requestId" TEXT,
  "spotifySongId" TEXT,
  "partyId" INTEGER,
  "userId" TEXT,
  "time" TEXT,
  "isQueued" BOOLEAN,
  PRIMARY KEY ("requestId"),
  FOREIGN KEY ("partyId") REFERENCES Party(partyId)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("userId") REFERENCES User(userId)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "RequestVotes"(
  "requestId" TEXT,
  "userId" TEXT,
  "type" TEXT,
  PRIMARY KEY ("requestId", "userId"),
  FOREIGN KEY ("requestId") REFERENCES SongRequest(requestId)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("userId") REFERENCES User(userId)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "PartyAttendee"(
  "partyId" INTEGER,
  "userId" TEXT,
  "type" TEXT,
  PRIMARY KEY ("partyId", "userId"),
  FOREIGN KEY ("partyId") REFERENCES Party(partyId)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("userId") REFERENCES User(userId)
    ON DELETE CASCADE ON UPDATE CASCADE
);

