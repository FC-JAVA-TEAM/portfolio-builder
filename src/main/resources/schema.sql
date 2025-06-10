DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS subsections;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS job_required_skills;
DROP TABLE IF EXISTS job_applications;
DROP TABLE IF EXISTS job_postings;
DROP TABLE IF EXISTS role_requests;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE job_postings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    department VARCHAR(100),
    employment_type VARCHAR(50),
    location VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE job_required_skills (
    job_posting_id BIGINT NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_posting_id, skill),
    FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE
);

CREATE TABLE job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_posting_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    resume_url TEXT,
    cover_letter TEXT,
    applied_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (job_posting_id) REFERENCES job_postings(id),
    FOREIGN KEY (applicant_id) REFERENCES users(id)
);

CREATE TABLE role_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    justification TEXT,
    requested_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    processed_by BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (processed_by) REFERENCES users(id)
);

CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    name VARCHAR(255),
    bio TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_profiles_user_id ON profiles(user_id);

CREATE TABLE sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT,
    type VARCHAR(50),
    title VARCHAR(255),
    display_order INT,
    background_url VARCHAR(255),
    FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_sections_profile_id ON sections(profile_id);

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
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    device_info VARCHAR(255),
    family VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    token_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (token_id) REFERENCES refresh_tokens(id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_HR');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
