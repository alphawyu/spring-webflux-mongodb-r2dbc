CREATE TABLE IF NOT EXISTS app_user (
    id bigint auto_increment primary key,
    username varchar(100) NOT NULL,
    email varchar(255),
    encoded_password varchar(255),
    image varchar(1024),
    bio varchar(1024),
    following_ids_str varchar(16384),
    favorite_articles_ids_str varchar(16384)
);
