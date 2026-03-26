package app;

import app.services.BmiServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    //free
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBMIVerdictNormal() {
        // Arrange
        double bmi = 22.0;
        String expectedBMI = "Normalvægtig";
        // Act
        String actualBMI = BmiServices.getBMIVerdict(bmi);
        // Assert
        assertEquals(expectedBMI, actualBMI);
    }

    @Test
    void getBMIVerdictSkinny() {
        // Arrange
        double bmi = 18.0;
        String expectedBMI = "Undervægtig";
        // Act
        String actualBMI = BmiServices.getBMIVerdict(bmi);
        // Assert
        assertEquals(expectedBMI, actualBMI);
    }

    @Test
    void getBMI() {
        double bmi = BmiServices.getBMI(72, 184);
    }
}