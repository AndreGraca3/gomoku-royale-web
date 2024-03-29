drop table if exists board;
drop table if exists match;
drop table if exists token;
drop table if exists stats;
drop table if exists "user";
drop table if exists rank;

create table if not exists rank
(
    name     varchar(20) primary key,
    icon_url TEXT unique not null,
    min_mmr  int         not null
);

create table if not exists "user"
(
    id         int generated always as identity primary key,
    name       varchar(20),
    email      varchar(200) unique not null,
    password   varchar(30),
    role       varchar(5)          not null check ( role in ('user', 'admin') ) default 'user',
    mmr        int                                                              default 0 not null check ( mmr >= 0 ),
    avatar_url TEXT,
    created_at timestamp           not null                                     default now()
);

create table if not exists token
(
    token_value VARCHAR(256) primary key,
    created_at  timestamp not null default now(),
    last_used   timestamp not null default now(),
    user_id     int references "user" (id) on delete cascade
);

create table if not exists match
(
    id         VARCHAR(256)                        default gen_random_uuid() primary key,
    isPrivate  Boolean                    not null,
    variant    VARCHAR(20)                not null,
    created_at timestamp                  not null default now(),
    black_id   int references "user" (id) on delete set null,
    white_id   int references "user" (id) on delete set null,
    state      VARCHAR(20)                not null
);

create table if not exists board
(
    match_id VARCHAR(256) primary key references match (id) on delete cascade,
    turn     Char         not null,
    size     int          not null,
    stones   VARCHAR(256) not null,
    type     VARCHAR(20)  not null
);

create table if not exists stats
(
    user_id          int references "user" (id) on delete cascade primary key,
    rank             varchar(20) references rank (name) not null default 'Bronze',
    matches_as_black int                                         default 0,
    wins_as_black    int                                         default 0,
    matches_as_white int                                         default 0,
    wins_as_white    int                                         default 0,
    draws            int                                         default 0
);