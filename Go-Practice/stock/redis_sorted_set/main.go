package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"strconv"
	"time"

	"github.com/go-redis/redis/v8"
	"github.com/segmentio/kafka-go" // Kafka 라이브러리 추가
)

var ctx = context.Background()
var rdb *redis.Client
var kafkaWriter *kafka.Writer // Kafka Writer 추가

// StockDecreaseMessage defines the structure of the message to be sent to Kafka.
type StockDecreaseMessage struct {
	ItemID   string `json:"item_id"`
	Quantity int64  `json:"quantity"`
	// Add other relevant fields like OrderID, UserID, etc.
}

func init() {
	rdb = redis.NewClient(&redis.Options{
		Addr:     "localhost:6379", // Redis 서버 주소
		Password: "",               // Redis 비밀번호 (없으면 "")
		DB:       0,                // 사용할 DB 번호
	})

	// Redis 연결 테스트
	_, err := rdb.Ping(ctx).Result()
	if err != nil {
		log.Fatalf("Could not connect to Redis: %v", err)
	}
	fmt.Println("Connected to Redis!")

	// Kafka Writer 초기화
	kalkaWriter = &kafka.Writer{
		Addr:     kafka.TCP("localhost:9092"), // Kafka 브로커 주소
		Topic:    "stock-decrease-requests",    // 사용할 Kafka 토픽
		Balancer: &kafka.LeastBytes{},
	}
	fmt.Println("Kafka Producer initialized!")
}

// AddStock adds stock for a given item.
// itemID: The ID of the item.
// quantity: The amount of stock to add.
func AddStock(itemID string, quantity int64) error {
	_, err := rdb.ZIncrBy(ctx, "inventory", float64(quantity), itemID).Result()
	if err != nil {
		return fmt.Errorf("failed to add stock: %w", err)
	}
	return nil
}

// RequestStockDecrease sends a stock decrease request to Kafka.
// itemID: The ID of the item.
// quantity: The amount of stock to decrease.
func RequestStockDecrease(itemID string, quantity int64) error {
	msg := StockDecreaseMessage{
		ItemID:   itemID,
		Quantity: quantity,
	}
	msgBytes, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("failed to marshal message: %w", err)
	}

	err = kafkaWriter.WriteMessages(ctx,
		kafka.Message{
			Key:   []byte(itemID),
			Value: msgBytes,
		},
	)
	if err != nil {
		return fmt.Errorf("failed to write message to Kafka: %w", err)
	}
	return nil
}

// GetStock retrieves the current stock for a given item.
// itemID: The ID of the item.
// Returns the current stock quantity.
func GetStock(itemID string) (int64, error) {
	score, err := rdb.ZScore(ctx, "inventory", itemID).Result()
	if err == redis.Nil {
		return 0, nil // Item not found, consider stock as 0
	} else if err != nil {
		return 0, fmt.Errorf("failed to get stock: %w", err)
	}
	return int64(score), nil
}

func main() {
	defer kafkaWriter.Close() // main 함수 종료 시 Kafka Writer 닫기

	// Example Usage
	itemID := "product:123"

	// Add initial stock
	err := AddStock(itemID, 100)
	if err != nil {
		log.Fatalf("Error adding stock: %v", err)
	}
	fmt.Printf("Added 100 stock for %s\n", itemID)

	// Get current stock
	stock, err := GetStock(itemID)
	if err != nil {
		log.Fatalf("Error getting stock: %v", err)
	}
	fmt.Printf("Current stock for %s: %d\n", itemID, stock)

	// Request to decrease stock via Kafka
	fmt.Println("\nRequesting stock decrease via Kafka...")
	err = RequestStockDecrease(itemID, 30)
	if err != nil {
		log.Fatalf("Error requesting stock decrease: %v", err)
	}
	fmt.Printf("Sent stock decrease request for %s, quantity 30 to Kafka\n", itemID)

	// Note: Stock will not be decreased immediately in Redis.
	// It will be processed by a separate Kafka consumer.
	stock, err = GetStock(itemID)
	if err != nil {
		log.Fatalf("Error getting stock: %v", err)
	}
	fmt.Printf("Current stock for %s (before consumer processing): %d\n", itemID, stock)

	// Simulate some delay for consumer to process
	time.Sleep(2 * time.Second)

	stock, err = GetStock(itemID)
	if err != nil {
		log.Fatalf("Error getting stock: %v", err)
	}
	fmt.Printf("Current stock for %s (after consumer processing - if consumer is running): %d\n", itemID, stock)
}