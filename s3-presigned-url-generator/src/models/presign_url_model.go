package models

import "time"

type PresignResult struct {
	URL     string    `json:"url"`
	Expires time.Time `json:"expires"`
	Method  string    `json:"method"`
}
