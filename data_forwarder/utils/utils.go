package utils

// ToPointer converts a value to a pointer to it.
func ToPointer[T any](value T) *T {
	return &value
}
