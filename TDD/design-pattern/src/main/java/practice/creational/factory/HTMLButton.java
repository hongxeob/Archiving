package practice.creational.factory;

import java.util.logging.Logger;

public class HTMLButton implements Button {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void render() {
        logger.info("<button>Test Button</button>");
        onClick();
    }

    @Override
    public void onClick() {
        logger.info("Click! Button says - 'Hello World'");
    }
}
