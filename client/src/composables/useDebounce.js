import { ref, watch } from 'vue'

/**
 * Composable for debouncing values
 * @param {Ref} value - The value to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {Ref} - Debounced value
 */
export function useDebounce(value, delay = 300) {
  const debouncedValue = ref(value.value)

  watch(value, (newValue) => {
    clearTimeout(debouncedValue._timeoutId)
    debouncedValue._timeoutId = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  return debouncedValue
}
