package practice.structural.compostie;

import java.util.ArrayList;
import java.util.List;

public class Directory implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> components = new ArrayList<>();

    public Directory(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        this.name = name;
    }

    @Override
    public void showDetails() {
        System.out.println("Directory: " + name);
        System.out.println("Contents:");
        for (FileSystemComponent component : components) {
            component.showDetails();
        }
    }

    @Override
    public long getSize() {
        long totalSize = 0L;
        for (FileSystemComponent component : components) {
            totalSize += component.getSize();
        }
        return totalSize;
    }

    public String getName() {
        return name;
    }

    public List<FileSystemComponent> getComponents() {
        return components;
    }

    public void addComponent(FileSystemComponent component) {
        components.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        components.remove(component);
    }
}
