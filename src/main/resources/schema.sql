-- ERD를 옮긴 DDL이다.
-- 바이너리 본문은 스토리지에 두므로 ERD의 binary_contents.bytes 컬럼은 만들지 않는다.
-- 애플리케이션이 삭제 순서를 직접 지키지만, 잘못된 경로로 행이 남지 않도록
-- ERD의 ON DELETE 규칙을 제약 조건으로도 걸어 둔다.

create table if not exists binary_contents
(
    id           uuid         primary key,
    created_at   timestamptz  not null,
    file_name    varchar(255) not null,
    size         bigint       not null,
    content_type varchar(100) not null
);

create table if not exists users
(
    id         uuid         primary key,
    created_at timestamptz  not null,
    updated_at timestamptz,
    username   varchar(50)  not null unique,
    email      varchar(100) not null unique,
    password   varchar(60)  not null,
    -- 프로필 이미지는 한 사용자만 가리킨다. 이미지가 지워지면 프로필이 없는 상태가 된다.
    profile_id uuid unique references binary_contents (id) on delete set null
);

create table if not exists user_statuses
(
    id             uuid        primary key,
    created_at     timestamptz not null,
    updated_at     timestamptz,
    -- 사용자당 하나뿐이고, 사용자가 사라지면 함께 사라진다.
    user_id        uuid        not null unique references users (id) on delete cascade,
    last_active_at timestamptz not null
);

create table if not exists channels
(
    id          uuid        primary key,
    created_at  timestamptz not null,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    -- PUBLIC / PRIVATE
    type        varchar(10) not null
);

create table if not exists read_statuses
(
    id           uuid        primary key,
    created_at   timestamptz not null,
    updated_at   timestamptz,
    user_id      uuid        not null references users (id) on delete cascade,
    channel_id   uuid        not null references channels (id) on delete cascade,
    last_read_at timestamptz not null,
    -- 한 사용자는 한 채널에 읽음 상태를 하나만 가진다.
    constraint uk_read_statuses_user_channel unique (user_id, channel_id)
);

create table if not exists messages
(
    id         uuid        primary key,
    created_at timestamptz not null,
    updated_at timestamptz,
    content    text,
    channel_id uuid        not null references channels (id) on delete cascade,
    -- 작성자가 탈퇴해도 메시지는 남고 작성자만 비워진다.
    author_id  uuid references users (id) on delete set null
);

create table if not exists message_attachments
(
    message_id    uuid not null references messages (id) on delete cascade,
    -- 첨부는 메시지 하나에만 속한다.
    attachment_id uuid not null unique references binary_contents (id) on delete cascade,
    primary key (message_id, attachment_id)
);

-- 조회 경로에 맞춘 인덱스.
-- read_statuses(user_id, channel_id) 유니크 제약이 user_id 조회까지 받아주므로 channel_id만 따로 만든다.
create index if not exists idx_read_statuses_channel_id on read_statuses (channel_id);
-- 채널의 메시지를 최근 순으로 페이지 단위로 읽는다.
create index if not exists idx_messages_channel_id_created_at on messages (channel_id, created_at desc);
