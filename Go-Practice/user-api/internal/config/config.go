package config

import (
	"os"
	"strconv"
)

type Config struct {
	Server   ServerConfig
	Database DatabaseConfig
	Logger   LoggerConfig
}

type ServerConfig struct {
	Port         string
	ReadTimeout  int
	WriteTimeout int
}

type DatabaseConfig struct {
	Host     string
	Port     string
	User     string
	Password string
	Name     string
	SSLMode  string
}
type LoggerConfig struct {
	Level string
	Env   string
}

func NewConfig() (*Config, error) {
	return &Config{
		Server: ServerConfig{
			Port:         getEnv("SERVER_PORT", "8080"),
			ReadTimeout:  getEnvAsInt("SERVER_READ_TIMEOUT", 15),
			WriteTimeout: getEnvAsInt("SERVER_WRITE_TIMEOUT", 15),
		},
		Database: DatabaseConfig{
			Host:     getEnv("DB_HOST", "localhost"),
			Port:     getEnv("DB_PORT", "3307"),
			User:     getEnv("DB_USER", "golang-user"),
			Password: getEnv("DB_PASSWORD", "password"),
			Name:     getEnv("DB_NAME", "userdb"),
			SSLMode:  getEnv("DB_SSL_MODE", "disable"),
		},
		Logger: LoggerConfig{
			Level: getEnv("LOG_LEVEL", "info"),
			Env:   getEnv("APP_ENV", "development"),
		},
	}, nil
}
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

func getEnvAsInt(key string, defaultValue int) int {
	if value := os.Getenv(key); value != "" {
		if intValue, err := strconv.Atoi(value); err == nil {
			return intValue
		}
	}
	return defaultValue
}
