CREATE TABLE IF NOT EXISTS salaries (
    id SERIAL PRIMARY KEY,
    grade TEXT NOT NULL,
    minSalary INTEGER,
    maxSalary INTEGER
);

