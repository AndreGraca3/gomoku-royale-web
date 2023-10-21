TRUNCATE board CASCADE;
TRUNCATE match CASCADE;
TRUNCATE token CASCADE;
TRUNCATE "user" CASCADE;
TRUNCATE rank CASCADE;

INSERT INTO rank (name, icon_url, min_mmr)
VALUES ('Bronze', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-1.png', 0),
       ('Silver', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-4.png', 50),
       ('Gold', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-7.png', 100),
       ('Platinum', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-10.png', 150),
       ('Diamond', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-13.png', 200),
       ('Champion', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s4-16.png', 250),
       ('Grand Champion', 'https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank19.png', 300);

INSERT INTO "user" (name, email, password, role, mmr, avatar_url, created_at, rank)
VALUES ('Daniel', 'daniel@gmail.com', 'password123', 'user', 120, 'daniel_avatar_url', NOW(), 'Bronze'),
       ('Diogo', 'diogo@gmail.com', 'password123', 'user', 80, 'diogo_avatar_url', NOW(), 'Silver'),
       ('Andre', 'andre@gmail.com', 'password', 'admin', 210, 'andre_avatar_url', NOW(), 'Grand Champion');

INSERT INTO token (token_value, created_at, last_used, user_id)
VALUES ('0Txy7bYpM9fZaEjKsLpQrVwXuT6jM0fD', NOW(), NOW(), 1),
       ('5Rz2vWqFpYhN6sTbGmCjXeZrU0gO4oA1', NOW(), NOW(), 2),
       ('9PwQ3zHsUeLmWxN7aRyV2bYjO5iK8tSf', NOW(), NOW(), 3);


SELECT id,
       isPrivate,
       variant,
       black_id,
       white_id,
       state,
       type,
       size,
       stones,
       turn
FROM match
         join board on match.id = board.match_id
WHERE match.id = '1';