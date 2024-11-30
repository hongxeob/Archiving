package practice.ch1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("홍길동");
    }

    @Test
    @DisplayName("고객 이름이 올바르게 반환되어야 한다")
    void getName_ShouldReturnCorrectName() {
        assertEquals("홍길동", customer.getName());
    }

    @Nested
    @DisplayName("대여료 계산 테스트")
    class RentalCalculationTests {

        @Test
        @DisplayName("대여 내역이 없는 경우 기본 문자열을 반환해야 한다")
        void statement_WithNoRentals_ShouldReturnDefaultStatement() {
            String expected = "홍길동 고객님의 대여기록 \n" +
                    "누적 대여료: 0.0\n" +
                    "적립 포인트: 0";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("일반 영화 대여시 2일 이하는 기본 요금만 부과되어야 한다")
        void statement_WithRegularMovieUnderTwoDays_ShouldChargeBasePrice() {
            Movie regularMovie = new Movie("일반 영화", Movie.REGULAR);
            customer.addRental(new Rental(regularMovie, 2));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t일반 영화\t2.0\n" +
                    "누적 대여료: 2.0\n" +
                    "적립 포인트: 1";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("일반 영화 대여시 2일 초과는 추가 요금이 부과되어야 한다")
        void statement_WithRegularMovieOverTwoDays_ShouldChargeExtraFee() {
            Movie regularMovie = new Movie("일반 영화", Movie.REGULAR);
            customer.addRental(new Rental(regularMovie, 4));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t일반 영화\t5.0\n" +
                    "누적 대여료: 5.0\n" +
                    "적립 포인트: 1";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("신작 영화는 대여 기간에 비례한 요금이 부과되어야 한다")
        void statement_WithNewRelease_ShouldChargePerDay() {
            Movie newMovie = new Movie("신작 영화", Movie.NEW_RELEASE);
            customer.addRental(new Rental(newMovie, 3));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t신작 영화\t9.0\n" +
                    "누적 대여료: 9.0\n" +
                    "적립 포인트: 2";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("어린이 영화 대여시 3일 이하는 기본 요금만 부과되어야 한다")
        void statement_WithChildrenMovieUnderThreeDays_ShouldChargeBasePrice() {
            Movie childrenMovie = new Movie("어린이 영화", Movie.CHILDREN);
            customer.addRental(new Rental(childrenMovie, 3));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t어린이 영화\t1.5\n" +
                    "누적 대여료: 1.5\n" +
                    "적립 포인트: 1";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("어린이 영화 대여시 3일 초과는 추가 요금이 부과되어야 한다")
        void statement_WithChildrenMovieOverThreeDays_ShouldChargeExtraFee() {
            Movie childrenMovie = new Movie("어린이 영화", Movie.CHILDREN);
            customer.addRental(new Rental(childrenMovie, 5));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t어린이 영화\t4.5\n" +
                    "누적 대여료: 4.5\n" +
                    "적립 포인트: 1";
            assertEquals(expected, customer.statement());
        }
    }

    @Nested
    @DisplayName("복합 대여 테스트")
    class MultipleRentalsTests {

        @Test
        @DisplayName("여러 종류의 영화를 대여할 경우 총합이 올바르게 계산되어야 한다")
        void statement_WithMultipleMovies_ShouldCalculateCorrectTotal() {
            customer.addRental(new Rental(new Movie("일반 영화", Movie.REGULAR), 3));        // 3.5
            customer.addRental(new Rental(new Movie("신작 영화", Movie.NEW_RELEASE), 2));    // 6.0
            customer.addRental(new Rental(new Movie("어린이 영화", Movie.CHILDREN), 4));     // 3.0

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t일반 영화\t3.5\n" +
                    "\t신작 영화\t6.0\n" +
                    "\t어린이 영화\t3.0\n" +
                    "누적 대여료: 12.5\n" +
                    "적립 포인트: 4";
            assertEquals(expected, customer.statement());
        }
    }

    @Nested
    @DisplayName("포인트 적립 테스트")
    class FrequentRenterPointsTests {

        @Test
        @DisplayName("신작 영화 2일 이상 대여시 추가 포인트가 적립되어야 한다")
        void statement_WithNewReleaseOverOneDay_ShouldEarnBonusPoint() {
            customer.addRental(new Rental(new Movie("신작 영화", Movie.NEW_RELEASE), 2));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t신작 영화\t6.0\n" +
                    "누적 대여료: 6.0\n" +
                    "적립 포인트: 2";
            assertEquals(expected, customer.statement());
        }

        @Test
        @DisplayName("신작 영화 1일 대여시 기본 포인트만 적립되어야 한다")
        void statement_WithNewReleaseOneDay_ShouldEarnBasePoint() {
            customer.addRental(new Rental(new Movie("신작 영화", Movie.NEW_RELEASE), 1));

            String expected = "홍길동 고객님의 대여기록 \n" +
                    "\t신작 영화\t3.0\n" +
                    "누적 대여료: 3.0\n" +
                    "적립 포인트: 1";
            assertEquals(expected, customer.statement());
        }
    }
}
