 alter table documents
 add column size_byte int,
  add column  content_type VARCHAR(255),
  add column  sha256 VARCHAR(255),
  add column  original_file_name VARCHAR(255);