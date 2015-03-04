CREATE TABLE news_article (
  id identity, 
  news_group_id int NOT NULL,
  state int NOT NULL,
  source_url varchar(2048) NOT NULL,
  source_title varchar(2048) NOT NULL,
  uri varchar(2048) NOT NULL,
  title varchar(2048) NOT NULL,
  ext_title varchar(2048) NOT NULL,
  url varchar(2048) NOT NULL,
  descr varchar(2048) NOT NULL,
  author varchar(1024) NOT NULL,
  categories varchar(1024) NOT NULL,
  published_date timestamp NOT NULL,
  updated_date timestamp NOT NULL,
  content varchar(65536) NOT NULL
);
