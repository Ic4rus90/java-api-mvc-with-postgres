CREATE TABLE IF NOT EXISTS salaries (
    id SERIAL PRIMARY KEY,
    grade TEXT NOT NULL,
    min_salary INT,
    max_salary INT
);

