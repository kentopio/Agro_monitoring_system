DROP DATABASE IF EXISTS agro_monitoring_db;
CREATE DATABASE agro_monitoring_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agro_monitoring_db;

CREATE TABLE fields (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        crop_type VARCHAR(100) NOT NULL,
                        area DECIMAL(10,2) NOT NULL
);

CREATE TABLE observations (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              field_id INT NOT NULL,
                              observation_date DATE NOT NULL,
                              inspector_name VARCHAR(100) NOT NULL,
                              FOREIGN KEY (field_id) REFERENCES fields(id) ON DELETE CASCADE
);

CREATE TABLE problems (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          observation_id INT NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          status ENUM('ВИЯВЛЕНО', 'ПІДТВЕРДЖЕНО', 'ОБРОБКУ_ЗАПЛАНОВАНО', 'УСУНЕНО') DEFAULT 'ВИЯВЛЕНО',
                          f1_infected_area INT NOT NULL CHECK (f1_infected_area BETWEEN 0 AND 100),
                          f2_severity INT NOT NULL CHECK (f2_severity BETWEEN 0 AND 100),
                          f3_sensitivity INT NOT NULL CHECK (f3_sensitivity BETWEEN 0 AND 100),
                          FOREIGN KEY (observation_id) REFERENCES observations(id) ON DELETE CASCADE
);

CREATE TABLE treatments (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            problem_id INT NOT NULL,
                            treatment_name VARCHAR(150) NOT NULL,
                            planned_date DATE NOT NULL,
                            is_completed BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

INSERT INTO fields (name, crop_type, area) VALUES
                                               ('Поле Сонячне', 'Пшениця озима', 120.50),
                                               ('Поле Північне', 'Кукурудза', 85.00);

INSERT INTO observations (field_id, observation_date, inspector_name) VALUES
                                                                          (1, '2026-10-15', 'Ілля Наретя'),
                                                                          (2, '2026-10-16', 'Петро Шевченко');

INSERT INTO problems (observation_id, description, status, f1_infected_area, f2_severity, f3_sensitivity) VALUES
                                                                                                              (1, 'Ураження листя борошнистою росою', 'ПІДТВЕРДЖЕНО', 30, 50, 80),
                                                                                                              (2, 'Поява кукурудзяного метелика', 'ВИЯВЛЕНО', 15, 25, 60);

INSERT INTO treatments (problem_id, treatment_name, planned_date, is_completed) VALUES
    (1, 'Обприскування фунгіцидом', '2026-10-20', FALSE);