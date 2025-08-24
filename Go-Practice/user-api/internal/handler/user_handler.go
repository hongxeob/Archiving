package handler

import (
	"encoding/json"
	"go.uber.org/zap"
	"net/http"
	"user-api/internal/service"
)

type UserHandler struct {
	usersService *service.UserService
	logger       *zap.Logger
}

func NewUserHandler(usersService *service.UserService, logger *zap.Logger) *UserHandler {
	return &UserHandler{
		usersService: usersService,
		logger:       logger,
	}
}

func (h *UserHandler) GetAllUsers(w http.ResponseWriter, r *http.Request) {
	h.logger.Info("GetAllUsers handler called")
	users, err := h.usersService.GetAllUsers(r.Context())
	if err != nil {
		h.respondWithError(w, http.StatusInternalServerError, "Failed to retrieve users", err)
		return
	}
	h.respondWithJSON(w, http.StatusOK, users)
}

func (h *UserHandler) respondWithJSON(w http.ResponseWriter, code int, payload interface{}) {
	response, _ := json.Marshal(payload)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(code)
	w.Write(response)
}

func (h *UserHandler) respondWithError(w http.ResponseWriter, code int, message string, err error) {
	h.logger.Error("API Error",
		zap.String("message", message),
		zap.Error(err),
		zap.Int("status_code", code),
	)

	errorResponse := map[string]interface{}{
		"error":   err.Error(),
		"code":    code,
		"message": message,
	}

	h.respondWithJSON(w, code, errorResponse)
}
