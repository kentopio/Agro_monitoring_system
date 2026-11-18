package com.agro;

import com.agro.models.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpAgroClient api = new HttpAgroClient();
    private final TableView<Field> fields = new TableView<>();
    private final TableView<Observation> observations = new TableView<>();
    private final TableView<Problem> problems = new TableView<>();
    private final TableView<Treatment> treatments = new TableView<>();
    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Поля", fieldPane()),
                new Tab("Спостереження", observationPane()),
                new Tab("Проблеми", problemPane()),
                new Tab("Заходи обробки", treatmentPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        stage.setScene(new Scene(tabs, 1080, 660));
        stage.setTitle("Інформаційна система моніторингу полів (Варіант №17)");
        stage.show();
        refreshAll();
    }

    private Pane fieldPane() {
        fields.getColumns().setAll(
                col("ID", "id"),
                col("Назва поля", "name"),
                col("Культура", "cropType"),
                col("Площа (га)", "area")
        );
        TextField name = new TextField(), cropType = new TextField(), area = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> {
            api.addField(new Field(0, name.getText(), cropType.getText(), Double.parseDouble(area.getText())));
            refreshFields();
        }));
        update.setOnAction(e -> safe(() -> {
            Field f = selected(fields);
            api.updateField(new Field(f.getId(), name.getText(), cropType.getText(), Double.parseDouble(area.getText())));
            refreshFields();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteField(selected(fields).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshFields());
        fields.getSelectionModel().selectedItemProperty().addListener((o, oldV, f) -> {
            if (f != null) {
                name.setText(f.getName());
                cropType.setText(f.getCropType());
                area.setText(String.valueOf(f.getArea()));
            }
        });
        VBox form = new VBox(8,
                new Label("Назва поля"), name,
                new Label("Культура"), cropType,
                new Label("Площа (га)"), area,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(fields, form);
    }

    private Pane observationPane() {
        observations.getColumns().setAll(
                col("ID", "id"),
                col("ID Поля", "fieldId"),
                col("Дата огляду", "observationDate"),
                col("Інспектор", "inspectorName")
        );
        TextField fieldId = new TextField(), date = new TextField("2026-10-25"), inspector = new TextField();
        ProgressBar progress = new ProgressBar(0);
        progress.setMaxWidth(Double.MAX_VALUE);
        Label status = new Label("CSV не запущено");
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити"),
                importCsv = new Button("Імпортувати CSV");
        add.setOnAction(e -> safe(() -> {
            api.addObservation(new Observation(0, Integer.parseInt(fieldId.getText()),
                    LocalDate.parse(date.getText()), inspector.getText()));
            refreshObservations();
        }));
        update.setOnAction(e -> safe(() -> {
            Observation obs = selected(observations);
            api.updateObservation(new Observation(obs.getId(), Integer.parseInt(fieldId.getText()),
                    LocalDate.parse(date.getText()), inspector.getText()));
            refreshObservations();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteObservation(selected(observations).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshObservations());
        observations.getSelectionModel().selectedItemProperty().addListener((o, oldV, obs) -> {
            if (obs != null) {
                fieldId.setText(String.valueOf(obs.getFieldId()));
                date.setText(obs.getObservationDate().toString());
                inspector.setText(obs.getInspectorName());
            }
        });
        importCsv.setOnAction(e -> {
            ImportObservationsTask task = new ImportObservationsTask(Path.of("observations_import.csv"), api);
            progress.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Готово. Імпортовано: " + task.getValue());
                refreshObservations();
            });
            task.setOnFailed(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                showError(task.getException());
            });
            new Thread(task, "csv-import").start();
        });
        VBox form = new VBox(8,
                new Label("ID Поля"), fieldId,
                new Label("Дата (YYYY-MM-DD)"), date,
                new Label("ПІБ Інспектора"), inspector,
                new HBox(8, add, update, delete, refresh),
                importCsv, progress, status
        );
        return layout(observations, form);
    }

    private Pane problemPane() {
        problems.getColumns().setAll(
                col("ID", "id"),
                col("ID Спостереж.", "observationId"),
                col("Опис проблеми", "description"),
                col("Стан", "status"),
                col("F1 (Площа)", "f1InfectedArea"),
                col("F2 (Тяжкість)", "f2Severity"),
                col("F3 (Чутлив.)", "f3Sensitivity"),
                col("Показник I", "integralIndicator")
        );
        TextField obsId = new TextField(), desc = new TextField(),
                f1 = new TextField(), f2 = new TextField(), f3 = new TextField();
        ComboBox<ProblemStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(ProblemStatus.values()));
        statusBox.setValue(ProblemStatus.ПІДТВЕРДЖЕНО);
        Button add = new Button("Створити"), update = new Button("Змінити"),
                changeState = new Button("Змінити стан"), delete = new Button("Видалити"),
                refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> {
            api.addProblem(new Problem(0, Integer.parseInt(obsId.getText()), desc.getText(),
                    ProblemStatus.ВИЯВЛЕНО, Integer.parseInt(f1.getText()),
                    Integer.parseInt(f2.getText()), Integer.parseInt(f3.getText())));
            refreshProblems();
        }));
        update.setOnAction(e -> safe(() -> {
            Problem p = selected(problems);
            api.updateProblem(new Problem(p.getId(), Integer.parseInt(obsId.getText()), desc.getText(),
                    p.getStatus(), Integer.parseInt(f1.getText()),
                    Integer.parseInt(f2.getText()), Integer.parseInt(f3.getText())));
            refreshProblems();
        }));
        changeState.setOnAction(e -> safe(() -> {
            api.changeProblemStatus(selected(problems).getId(), statusBox.getValue());
            refreshProblems();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteProblem(selected(problems).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshProblems());
        problems.getSelectionModel().selectedItemProperty().addListener((o, oldV, p) -> {
            if (p != null) {
                obsId.setText(String.valueOf(p.getObservationId()));
                desc.setText(p.getDescription());
                f1.setText(String.valueOf(p.getF1InfectedArea()));
                f2.setText(String.valueOf(p.getF2Severity()));
                f3.setText(String.valueOf(p.getF3Sensitivity()));
                statusBox.setValue(p.getStatus());
            }
        });
        VBox form = new VBox(6,
                new Label("ID Спостереження"), obsId,
                new Label("Опис проблеми"), desc,
                new Label("F1 — Частка ураженої площі (0-100)"), f1,
                new Label("F2 — Тяжкість прояву (0-100)"), f2,
                new Label("F3 — Чутливість культури (0-100)"), f3,
                new Label("Новий стан життєвого циклу"), statusBox,
                new HBox(6, add, update, changeState),
                new HBox(6, delete, refresh)
        );
        return layout(problems, form);
    }

    private Pane treatmentPane() {
        treatments.getColumns().setAll(
                col("ID", "id"),
                col("ID Проблеми", "problemId"),
                col("Назва заходу", "treatmentName"),
                col("Планова дата", "plannedDate"),
                col("Виконано", "completed")
        );
        TextField problemId = new TextField(), name = new TextField(), date = new TextField("2026-10-28");
        CheckBox completed = new CheckBox("Захід виконано");
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> {
            api.addTreatment(new Treatment(0, Integer.parseInt(problemId.getText()),
                    name.getText(), LocalDate.parse(date.getText()), completed.isSelected()));
            refreshTreatments();
        }));
        update.setOnAction(e -> safe(() -> {
            Treatment t = selected(treatments);
            api.updateTreatment(new Treatment(t.getId(), Integer.parseInt(problemId.getText()),
                    name.getText(), LocalDate.parse(date.getText()), completed.isSelected()));
            refreshTreatments();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteTreatment(selected(treatments).getId());
            refreshTreatments();
        }));
        refresh.setOnAction(e -> refreshTreatments());
        treatments.getSelectionModel().selectedItemProperty().addListener((o, oldV, t) -> {
            if (t != null) {
                problemId.setText(String.valueOf(t.getProblemId()));
                name.setText(t.getTreatmentName());
                date.setText(t.getPlannedDate().toString());
                completed.setSelected(t.isCompleted());
            }
        });
        VBox form = new VBox(8,
                new Label("ID Проблеми"), problemId,
                new Label("Назва заходу обробки"), name,
                new Label("Планова дата (YYYY-MM-DD)"), date,
                completed,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(treatments, form);
    }

    private void refreshAll() {
        refreshFields();
        refreshObservations();
        refreshProblems();
        refreshTreatments();
    }
    private void refreshFields() {
        safe(() -> fields.setItems(FXCollections.observableArrayList(api.getFields())));
    }
    private void refreshObservations() {
        safe(() -> observations.setItems(FXCollections.observableArrayList(api.getObservations())));
    }
    private void refreshProblems() {
        safe(() -> problems.setItems(FXCollections.observableArrayList(api.getProblems())));
    }
    private void refreshTreatments() {
        safe(() -> treatments.setItems(FXCollections.observableArrayList(api.getTreatments())));
    }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) throw new IllegalArgumentException("Виберіть запис у таблиці");
        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR,
                e == null ? "Помилка" : e.getMessage(), ButtonType.OK);
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.68);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }
    public static void main(String[] args) {
        launch(args);
    }
}