/**
 * v-reveal — Scroll-triggered reveal animation directive
 *
 * Usage:
 *   <div v-reveal>...</div>
 *   <div v-reveal:0.2>...</div>          // delay 0.2s
 *   <div v-reveal.scale>...</div>        // scale animation
 *   <div v-reveal.left>...</div>         // slide from left
 *   <div v-reveal.right>...</div>        // slide from right
 */

const OBSERVER_OPTIONS = {
  threshold: 0.12,
  rootMargin: '0px 0px -40px 0px'
}

// Map modifiers to CSS classes
const MODIFIER_MAP = {
  scale: 'reveal-scale',
  left: 'reveal-left',
  right: 'reveal-right'
}

function getAnimationClass(modifiers) {
  for (const key of Object.keys(MODIFIER_MAP)) {
    if (modifiers[key]) return MODIFIER_MAP[key]
  }
  return 'reveal'
}

let observer = null

function getObserver() {
  if (!observer) {
    observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
          observer.unobserve(entry.target)
        }
      })
    }, OBSERVER_OPTIONS)
  }
  return observer
}

export default {
  inserted(el, binding) {
    const animClass = getAnimationClass(binding.modifiers)
    el.classList.add(animClass)

    // Support delay via argument: v-reveal:0.3
    if (binding.arg) {
      const delay = parseFloat(binding.arg)
      if (!isNaN(delay)) {
        el.style.transitionDelay = `${delay}s`
      }
    }

    // Support delay via value: v-reveal="0.3"
    if (typeof binding.value === 'number') {
      el.style.transitionDelay = `${binding.value}s`
    }

    getObserver().observe(el)
  },
  unbind(el) {
    if (observer) {
      observer.unobserve(el)
    }
  }
}
