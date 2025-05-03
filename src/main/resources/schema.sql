CREATE TABLE currency (
  code    VARCHAR(3)   PRIMARY KEY,
  name    VARCHAR(255),
  rate    VARCHAR(255),
  updated TIMESTAMP
);