package practice.structural.compostie;

public record File(
        String name,
        int size
) implements FileSystemComponent {

    public File {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
    }

    @Override
    public void showDetails() {
        System.out.println("File name: " + name + ", File size: " + size);
    }

    @Override
    public long getSize() {
        return size;
    }
}
