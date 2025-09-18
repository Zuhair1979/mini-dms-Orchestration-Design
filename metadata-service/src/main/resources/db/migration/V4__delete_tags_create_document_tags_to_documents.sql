alter table documents drop column if EXISTS tags;

create table documents_tags(
document_id UUID not null references documents(id) on delete cascade,
tag varchar(255) not null
);
CREATE INDEX idx_documents_tags_doc ON documents_tags(document_id);
CREATE INDEX idx_documents_tags_tag ON documents_tags(tag);