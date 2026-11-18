package com.agro;

import com.agro.models.Observation;
import javafx.concurrent.Task;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class ImportObservationsTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportObservationsTask.class.getName());
    private final Path file;
    private final HttpAgroClient api;
    public ImportObservationsTask(Path file, HttpAgroClient api) {
        this.file = file;
        this.api = api;
    }
    @Override
    protected Integer call() throws Exception {
        Path target = Files.exists(file) ? file : Path.of("..", file.toString());
        List<String> lines = Files.readAllLines(target);
        int total = Math.max(1, lines.size() - 1);
        int imported = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = line.contains(";") ? line.split(";") : line.split(",");
            if (p.length != 3) {
                LOG.warning("Пропущено некоректний рядок CSV №" + (i + 1));
                continue;
            }
            Observation obs = new Observation(
                    0,
                    Integer.parseInt(p[0].trim()),
                    LocalDate.parse(p[1].trim()),
                    p[2].trim()
            );
            api.addObservation(obs);
            imported++;
            updateProgress(i, total);
            updateMessage("Імпортовано: " + imported + " із " + total);
        }
        return imported;
    }
}