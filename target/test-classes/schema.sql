DROP TABLE IF EXISTS subsections;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS profiles;

CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    bio TEXT,
    password VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE
);

CREATE TABLE sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT,
    type VARCHAR(50),
    title VARCHAR(255),
    display_order INT,
    background_url VARCHAR(255),
    FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);

CREATE TABLE subsections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    section_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    display_order INT,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    device_info VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES profiles(user_id) ON DELETE CASCADE
);
