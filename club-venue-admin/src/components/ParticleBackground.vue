<template>
  <div ref="container" class="particle-bg"></div>
</template>

<script>
import * as THREE from 'three'

// ═══════════════════════════════════════════════════════
//  Shared glow texture (reused across all instances)
// ═══════════════════════════════════════════════════════
let _sharedTexture = null
function getGlowTexture() {
  if (_sharedTexture) return _sharedTexture
  const size = 64
  const canvas = document.createElement('canvas')
  canvas.width = size; canvas.height = size
  const ctx = canvas.getContext('2d')
  const half = size / 2
  const g = ctx.createRadialGradient(half, half, 0, half, half, half)
  g.addColorStop(0, 'rgba(255,255,255,1)')
  g.addColorStop(0.06, 'rgba(255,255,255,0.95)')
  g.addColorStop(0.2, 'rgba(255,255,255,0.6)')
  g.addColorStop(0.45, 'rgba(255,255,255,0.15)')
  g.addColorStop(0.7, 'rgba(255,255,255,0.02)')
  g.addColorStop(1, 'rgba(255,255,255,0)')
  ctx.fillStyle = g
  ctx.fillRect(0, 0, size, size)
  _sharedTexture = new THREE.CanvasTexture(canvas)
  return _sharedTexture
}

// ═══════════════════════════════════════════════════════
//  Create a ring of particles along a circle
// ═══════════════════════════════════════════════════════
function createParticleRing(radius, count, color, tiltX, tiltY, tiltZ, size, opacity) {
  const positions = new Float32Array(count * 3)
  const colors = new Float32Array(count * 3)
  const c = new THREE.Color(color)
  for (let i = 0; i < count; i++) {
    const angle = (Math.PI * 2 * i) / count
    positions[i * 3]     = Math.cos(angle) * radius
    positions[i * 3 + 1] = 0
    positions[i * 3 + 2] = Math.sin(angle) * radius
    // Slight brightness variation
    const bright = 0.7 + Math.random() * 0.3
    colors[i * 3]     = c.r * bright
    colors[i * 3 + 1] = c.g * bright
    colors[i * 3 + 2] = c.b * bright
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geo.setAttribute('color',    new THREE.BufferAttribute(colors, 3))
  const mat = new THREE.PointsMaterial({
    size, map: getGlowTexture(), vertexColors: true,
    blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
    transparent: true, opacity,
  })
  const ring = new THREE.Points(geo, mat)
  const group = new THREE.Group()
  group.rotation.x = tiltX
  group.rotation.y = tiltY
  group.rotation.z = tiltZ
  group.add(ring)
  return { group, points: ring, geometry: geo, material: mat }
}

export default {
  name: 'ParticleBackground',

  props: {
    /* ──── 粒子云 ──── */
    particleCount:       { type: Number,  default: 280 },
    particleSize:        { type: Number,  default: 3.2 },
    opacity:             { type: Number,  default: 0.6 },
    speed:               { type: Number,  default: 0.3 },
    driftSpeed:          { type: Number,  default: 0.08 },
    spreadX:             { type: Number,  default: 500 },
    spreadY:             { type: Number,  default: 500 },
    spreadZ:             { type: Number,  default: 350 },
    connectionDistance:  { type: Number,  default: 100 },
    lineOpacity:         { type: Number,  default: 0.1 },
    enableParallax:      { type: Boolean, default: true },
    color1:              { type: String,  default: '#1d1d1f' },
    color2:              { type: String,  default: '#424245' },

    /* ──── 中央能量核心 ──── */
    showCore:            { type: Boolean, default: false },
    coreParticleCount:   { type: Number,  default: 500 },
    coreRadius:          { type: Number,  default: 55 },
    coreSize:            { type: Number,  default: 4.5 },
    coreColor:           { type: String,  default: '#ffffff' },

    /* ──── 轨道环 ──── */
    orbitCount:          { type: Number,  default: 0 },
    orbitRadius:         { type: Number,  default: 150 },
    orbitColor:          { type: String,  default: '#1d1d1f' },
    orbitOpacity:        { type: Number,  default: 0.7 },
    orbitSpeed:          { type: Number,  default: 0.5 },

    /* ──── 垂直光束 ──── */
    showBeams:           { type: Boolean, default: false },
    beamCount:           { type: Number,  default: 60 },
    beamLength:          { type: Number,  default: 300 },
    beamColor:           { type: String,  default: '#1d1d1f' },

    /* ──── 脉冲波纹 ──── */
    showPulse:           { type: Boolean, default: false },
    pulseColor:          { type: String,  default: '#1d1d1f' },
    pulseMaxRadius:      { type: Number,  default: 280 },

    /* ──── 线框几何体 ──── */
    showWireframe:       { type: Boolean, default: false },
    wireframeColor:      { type: String,  default: '#1d1d1f' },
    wireframeOpacity:    { type: Number,  default: 0.25 },
  },

  data() {
    return { animId: null }
  },

  mounted() { this.init() },
  beforeDestroy() { this.cleanup() },

  methods: {
    // ═══════════════════════════════════════════════
    //  init — 构建整个科幻场景
    // ═══════════════════════════════════════════════
    init() {
      const container = this.$refs.container
      if (!container) return
      let w = container.clientWidth, h = container.clientHeight
      if (w === 0 || h === 0) return

      // ── Scene / Camera / Renderer ──
      const scene = new THREE.Scene()
      const camera = new THREE.PerspectiveCamera(50, w / h, 10, 2500)
      camera.position.set(0, 0, 500)
      const renderer = new THREE.WebGLRenderer({
        alpha: true, antialias: true,
        powerPreference: 'high-performance',
      })
      const dpr = Math.min(window.devicePixelRatio || 1, 2)
      renderer.setPixelRatio(dpr)
      renderer.setSize(w, h)
      renderer.setClearColor(0x000000, 0)
      container.appendChild(renderer.domElement)

      // ── Parent group (slow global rotation) ──
      const world = new THREE.Group()
      scene.add(world)

      // ═══════════════════════════════════════════
      //  1. 中央能量核心
      // ═══════════════════════════════════════════
      let coreGroup = null, coreGeometry = null, coreMaterial = null
      if (this.showCore) {
        const cc = this.coreParticleCount
        const cr = this.coreRadius
        const pos = new Float32Array(cc * 3)
        const col = new Float32Array(cc * 3)
        const white = new THREE.Color('#ffffff')
        const cyan  = new THREE.Color(this.coreColor)

        for (let i = 0; i < cc; i++) {
          // 球形分布（表面集中 + 内部填充）
          const theta = Math.random() * Math.PI * 2
          const phi   = Math.acos(2 * Math.random() - 1)
          const r     = cr * (0.4 + Math.random() * 0.6) // 40%-100% radius
          pos[i * 3]     = Math.sin(phi) * Math.cos(theta) * r
          pos[i * 3 + 1] = Math.sin(phi) * Math.sin(theta) * r
          pos[i * 3 + 2] = Math.cos(phi) * r

          // 从白到青渐变（外围更青）
          const t = r / cr
          const mixed = white.clone().lerp(cyan, t * 0.8)
          col[i * 3]     = mixed.r
          col[i * 3 + 1] = mixed.g
          col[i * 3 + 2] = mixed.b
        }

        coreGeometry = new THREE.BufferGeometry()
        coreGeometry.setAttribute('position', new THREE.BufferAttribute(pos, 3))
        coreGeometry.setAttribute('color',    new THREE.BufferAttribute(col, 3))
        coreMaterial = new THREE.PointsMaterial({
          size: this.coreSize, map: getGlowTexture(), vertexColors: true,
          blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
          transparent: true, opacity: 0.9,
        })
        const corePoints = new THREE.Points(coreGeometry, coreMaterial)
        coreGroup = new THREE.Group()
        coreGroup.add(corePoints)
        world.add(coreGroup)
      }

      // ═══════════════════════════════════════════
      //  2. 轨道环（粒子构成的环）
      // ═══════════════════════════════════════════
      const orbits = []
      if (this.orbitCount > 0) {
        const tilts = [
          [0,           0, 0],
          [Math.PI * 0.42, 0, 0],
          [Math.PI * 0.25, Math.PI * 0.3, 0],
          [Math.PI * 0.55, Math.PI * 0.15, Math.PI * 0.2],
        ]
        const speeds = [0.6, -0.45, 0.35, -0.5]
        const radii  = [
          this.orbitRadius,
          this.orbitRadius * 1.18,
          this.orbitRadius * 0.88,
          this.orbitRadius * 1.3,
        ]
        for (let i = 0; i < this.orbitCount; i++) {
          const ring = createParticleRing(
            radii[i], 180,
            this.orbitColor,
            tilts[i][0], tilts[i][1], tilts[i][2],
            2.2, this.orbitOpacity * (1 - i * 0.15),
          )
          world.add(ring.group)
          orbits.push({ ...ring, speed: speeds[i] * this.orbitSpeed })
        }
      }

      // ═══════════════════════════════════════════
      //  3. 背景粒子云 + 连线
      // ═══════════════════════════════════════════
      const bgCount = this.particleCount
      const bgPositions = new Float32Array(bgCount * 3)
      const bgColors    = new Float32Array(bgCount * 3)
      const c1 = new THREE.Color(this.color1)
      const c2 = new THREE.Color(this.color2)

      for (let i = 0; i < bgCount; i++) {
        // 球形分布让粒子围绕中心
        const theta = Math.random() * Math.PI * 2
        const phi   = Math.acos(2 * Math.random() - 1)
        const r     = 120 + Math.random() * 280
        bgPositions[i * 3]     = Math.sin(phi) * Math.cos(theta) * r
        bgPositions[i * 3 + 1] = Math.sin(phi) * Math.sin(theta) * r
        bgPositions[i * 3 + 2] = Math.cos(phi) * r

        const t = Math.random()
        bgColors[i * 3]     = c1.r + (c2.r - c1.r) * t
        bgColors[i * 3 + 1] = c1.g + (c2.g - c1.g) * t
        bgColors[i * 3 + 2] = c1.b + (c2.b - c1.b) * t
      }

      const bgGeometry = new THREE.BufferGeometry()
      bgGeometry.setAttribute('position', new THREE.BufferAttribute(bgPositions, 3))
      bgGeometry.setAttribute('color',    new THREE.BufferAttribute(bgColors, 3))
      const bgMaterial = new THREE.PointsMaterial({
        size: this.particleSize, map: getGlowTexture(), vertexColors: true,
        blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
        transparent: true, opacity: this.opacity,
      })
      const bgPoints = new THREE.Points(bgGeometry, bgMaterial)
      const cloudGroup = new THREE.Group()
      cloudGroup.add(bgPoints)
      world.add(cloudGroup)

      // 连线
      let lineGeometry = null, lineMesh = null
      if (this.connectionDistance > 0) {
        const { buf, count } = this._buildConnections(bgPositions, bgCount, this.connectionDistance)
        if (count > 0) {
          lineGeometry = new THREE.BufferGeometry()
          lineGeometry.setAttribute('position', new THREE.BufferAttribute(buf, 3))
          const lm = new THREE.LineBasicMaterial({
            color: new THREE.Color(this.color1),
            transparent: true, opacity: this.lineOpacity,
            blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
          })
          lineMesh = new THREE.LineSegments(lineGeometry, lm)
          cloudGroup.add(lineMesh)
        }
      }

      // 漂移速度
      const bgVelocities = this.driftSpeed > 0
        ? new Float32Array(bgCount * 3) : null
      if (bgVelocities) {
        const ds = this.driftSpeed
        for (let i = 0; i < bgCount * 3; i++) {
          bgVelocities[i] = (Math.random() - 0.5) * ds * 2
        }
      }

      // ═══════════════════════════════════════════
      //  4. 垂直光束
      // ═══════════════════════════════════════════
      let beamGroup = null, beamGeometry = null, beamPositions = null
      if (this.showBeams) {
        const bc = this.beamCount
        beamPositions = new Float32Array(bc * 3)
        const beamColorsArr = new Float32Array(bc * 3)
        const beamC = new THREE.Color(this.beamColor)
        const spreadR = 160
        for (let i = 0; i < bc; i++) {
          const angle = Math.random() * Math.PI * 2
          const r = 30 + Math.random() * spreadR
          beamPositions[i * 3]     = Math.cos(angle) * r
          beamPositions[i * 3 + 1] = (Math.random() - 0.5) * this.beamLength
          beamPositions[i * 3 + 2] = Math.sin(angle) * r
          const bright = 0.5 + Math.random() * 0.5
          beamColorsArr[i * 3]     = beamC.r * bright
          beamColorsArr[i * 3 + 1] = beamC.g * bright
          beamColorsArr[i * 3 + 2] = beamC.b * bright
        }
        beamGeometry = new THREE.BufferGeometry()
        beamGeometry.setAttribute('position', new THREE.BufferAttribute(beamPositions, 3))
        beamGeometry.setAttribute('color',    new THREE.BufferAttribute(beamColorsArr, 3))
        const beamMat = new THREE.PointsMaterial({
          size: 2.8, map: getGlowTexture(), vertexColors: true,
          blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
          transparent: true, opacity: 0.65,
        })
        const beamPoints = new THREE.Points(beamGeometry, beamMat)
        beamGroup = new THREE.Group()
        beamGroup.add(beamPoints)
        world.add(beamGroup)
      }

      // ═══════════════════════════════════════════
      //  5. 脉冲波纹
      // ═══════════════════════════════════════════
      let pulseRing = null, pulseGroup = null
      let pulseGeo = null, pulseMat = null
      if (this.showPulse) {
        const { group, points, geometry, material } = createParticleRing(
          10, 120, this.pulseColor,
          Math.PI / 2, 0, 0, // horizontal
          2.5, 0.8,
        )
        pulseRing = { group, points, geometry, material, currentRadius: 10 }
        world.add(group)
      }

      // ═══════════════════════════════════════════
      //  6. 线框几何体（二十面体）
      // ═══════════════════════════════════════════
      let wireframeGroup = null
      if (this.showWireframe) {
        const icoGeo = new THREE.IcosahedronGeometry(55, 1)
        const wireGeo = new THREE.WireframeGeometry(icoGeo)
        const wireMat = new THREE.LineBasicMaterial({
          color: new THREE.Color(this.wireframeColor),
          transparent: true, opacity: this.wireframeOpacity,
          blending: THREE.AdditiveBlending, depthWrite: false, depthTest: false,
        })
        const wireframe = new THREE.LineSegments(wireGeo, wireMat)
        wireframeGroup = new THREE.Group()
        wireframeGroup.add(wireframe)
        world.add(wireframeGroup)
        this._wireframeObj = wireframe
        this._wireframeMat = wireMat
        this._wireframeGeo = wireGeo
      }

      // ═══════════════════════════════════════════
      //  Mouse tracking
      // ═══════════════════════════════════════════
      const mouse = { x: 0, y: 0 }, target = { x: 0, y: 0 }
      const onMouseMove = (e) => {
        if (!this.enableParallax) return
        mouse.x = (e.clientX / window.innerWidth)  * 2 - 1
        mouse.y = -(e.clientY / window.innerHeight) * 2 + 1
      }
      window.addEventListener('mousemove', onMouseMove, { passive: true })

      // ═══════════════════════════════════════════
      //  Resize
      // ═══════════════════════════════════════════
      const onResize = () => {
        const nw = container.clientWidth, nh = container.clientHeight
        if (nw === 0 || nh === 0) return
        camera.aspect = nw / nh
        camera.updateProjectionMatrix()
        renderer.setSize(nw, nh)
      }
      let ro = null
      if (window.ResizeObserver) {
        ro = new ResizeObserver(() => onResize())
        ro.observe(container)
      }
      window.addEventListener('resize', onResize)

      // ═══════════════════════════════════════════
      //  Animation Loop
      // ═══════════════════════════════════════════
      const clock = new THREE.Clock()
      const animate = () => {
        this.animId = requestAnimationFrame(animate)
        const dt = Math.min(clock.getDelta(), 0.1)
        const elapsed = clock.elapsedTime

        target.x += (mouse.x - target.x) * 0.04
        target.y += (mouse.y - target.y) * 0.04

        // ── 全局缓慢旋转 ──
        world.rotation.y += this.speed * 0.0005
        if (this.enableParallax) {
          world.rotation.x += (target.y * 0.2 - world.rotation.x) * 0.012
          world.rotation.y += target.x * 0.001
        }

        // ── 核心脉动 ──
        if (coreGroup && coreMaterial) {
          const pulse = 1 + Math.sin(elapsed * 2.5) * 0.08 + Math.sin(elapsed * 4.7) * 0.04
          coreGroup.scale.setScalar(pulse)
          coreMaterial.opacity = 0.75 + Math.sin(elapsed * 3) * 0.2
        }

        // ── 轨道环旋转 ──
        orbits.forEach(o => {
          o.group.rotation.z += o.speed * 0.005
        })

        // ── 云层旋转（独立于 world）──
        cloudGroup.rotation.y += this.speed * 0.0006
        cloudGroup.rotation.x += this.speed * 0.0002

        // ── 光束粒子流 ──
        if (beamPositions && beamGeometry && beamGroup) {
          const bc = this.beamCount
          const halfLen = this.beamLength / 2
          for (let i = 0; i < bc; i++) {
            beamPositions[i * 3 + 1] += 0.4 + Math.random() * 0.6
            if (beamPositions[i * 3 + 1] > halfLen) {
              beamPositions[i * 3 + 1] = -halfLen
            }
          }
          beamGeometry.attributes.position.needsUpdate = true
          // 光束组缓慢自转
          beamGroup.rotation.y += 0.002
        }

        // ── 脉冲波纹扩散 ──
        if (pulseRing) {
          pulseRing.currentRadius += 1.2
          if (pulseRing.currentRadius > this.pulseMaxRadius) {
            pulseRing.currentRadius = 10
          }
          // 更新环的 scale（直接改 group scale）
          const s = pulseRing.currentRadius / 10
          pulseRing.group.scale.setScalar(s)
          // 透明度随半径衰减
          const fadeRatio = 1 - pulseRing.currentRadius / this.pulseMaxRadius
          pulseRing.material.opacity = 0.7 * fadeRatio
        }

        // ── 线框旋转 ──
        if (wireframeGroup) {
          wireframeGroup.rotation.x += 0.003
          wireframeGroup.rotation.y += 0.005
          wireframeGroup.rotation.z += 0.002
        }

        // ── 粒子漂移 ──
        if (bgVelocities) {
          this._applySphereDrift(bgPositions, bgVelocities, bgCount, 120, 400)
          bgGeometry.attributes.position.needsUpdate = true
          if (lineMesh && lineGeometry) {
            const { buf, count: lc } = this._buildConnections(bgPositions, bgCount, this.connectionDistance)
            if (lc > 0) {
              lineGeometry.setDrawRange(0, lc * 2)
              lineGeometry.attributes.position.array.set(buf)
              lineGeometry.attributes.position.needsUpdate = true
            } else {
              lineGeometry.setDrawRange(0, 0)
            }
          }
        }

        renderer.render(scene, camera)
      }
      animate()

      // ── 保存引用 ──
      Object.assign(this, {
        _scene: scene, _camera: camera, _renderer: renderer, _world: world,
        _bgGeometry: bgGeometry, _bgMaterial: bgMaterial, _bgPositions: bgPositions,
        _bgVelocities: bgVelocities, _cloudGroup: cloudGroup,
        _lineGeometry: lineGeometry, _lineMesh: lineMesh,
        _coreGroup: coreGroup, _coreGeometry: coreGeometry, _coreMaterial: coreMaterial,
        _orbits: orbits,
        _beamGroup: beamGroup, _beamGeometry: beamGeometry, _beamPositions: beamPositions,
        _pulseRing: pulseRing,
        _wireframeGroup: wireframeGroup,
        _onMouseMove: onMouseMove, _onResize: onResize,
        _resizeObserver: ro, _container: container,
      })
    },

    // ═══════════════════════════════════════════
    //  Build connection lines
    // ═══════════════════════════════════════════
    _buildConnections(positions, count, threshold) {
      const t2 = threshold * threshold
      const maxLines = Math.min(count * 6, 6000)
      const buf = new Float32Array(maxLines * 6)
      let lc = 0
      const stride = count > 300 ? 2 : 1
      for (let i = 0; i < count; i += stride) {
        const ix = positions[i * 3], iy = positions[i * 3 + 1], iz = positions[i * 3 + 2]
        for (let j = i + stride; j < count; j += stride) {
          const dx = ix - positions[j * 3]
          const dy = iy - positions[j * 3 + 1]
          const dz = iz - positions[j * 3 + 2]
          if (dx * dx + dy * dy + dz * dz < t2) {
            if (lc >= maxLines) break
            const k = lc * 6
            buf[k] = ix; buf[k+1] = iy; buf[k+2] = iz
            buf[k+3] = positions[j*3]; buf[k+4] = positions[j*3+1]; buf[k+5] = positions[j*3+2]
            lc++
          }
        }
        if (lc >= maxLines) break
      }
      return { buf, count: lc }
    },

    // ═══════════════════════════════════════════
    //  Sphere drift with inner/outer bounds
    // ═══════════════════════════════════════════
    _applySphereDrift(positions, velocities, count, minR, maxR) {
      for (let i = 0; i < count; i++) {
        const i3 = i * 3
        positions[i3]     += velocities[i3]
        positions[i3 + 1] += velocities[i3 + 1]
        positions[i3 + 2] += velocities[i3 + 2]

        const r = Math.sqrt(
          positions[i3] ** 2 + positions[i3 + 1] ** 2 + positions[i3 + 2] ** 2
        )
        // Bounce at bounds
        if (r < minR || r > maxR) {
          // Reverse and push back inward
          velocities[i3]     *= -1
          velocities[i3 + 1] *= -1
          velocities[i3 + 2] *= -1
          // Normalize and clamp
          const targetR = r < minR ? minR + 5 : maxR - 5
          const scale = targetR / (r || 1)
          positions[i3]     *= scale
          positions[i3 + 1] *= scale
          positions[i3 + 2] *= scale
        }
      }
    },

    // ═══════════════════════════════════════════
    //  Cleanup
    // ═══════════════════════════════════════════
    cleanup() {
      if (this.animId) { cancelAnimationFrame(this.animId); this.animId = null }
      window.removeEventListener('mousemove', this._onMouseMove)
      window.removeEventListener('resize',   this._onResize)
      if (this._resizeObserver) { this._resizeObserver.disconnect(); this._resizeObserver = null }
      if (this._renderer) {
        this._renderer.dispose()
        if (this._container) this._container.removeChild(this._renderer.domElement)
      }
      // Dispose all geometries & materials
      const geos = ['_bgGeometry','_lineGeometry','_coreGeometry','_beamGeometry']
      const mats = ['_bgMaterial','_coreMaterial']
      geos.forEach(k => { if (this[k]) this[k].dispose() })
      mats.forEach(k => { if (this[k]) this[k].dispose() })
      if (this._lineMesh)    this._lineMesh.material.dispose()
      if (this._orbits)      this._orbits.forEach(o => { o.geometry.dispose(); o.material.dispose() })
      if (this._pulseRing)   { this._pulseRing.geometry.dispose(); this._pulseRing.material.dispose() }
      if (this._wireframeObj) {
        this._wireframeObj.geometry.dispose()
        this._wireframeObj.material.dispose()
      }
      const keys = [
        '_scene','_camera','_renderer','_world',
        '_bgGeometry','_bgMaterial','_bgPositions','_bgVelocities','_cloudGroup',
        '_lineGeometry','_lineMesh',
        '_coreGroup','_coreGeometry','_coreMaterial',
        '_orbits',
        '_beamGroup','_beamGeometry','_beamPositions',
        '_pulseRing',
        '_wireframeGroup','_wireframeObj','_wireframeMat','_wireframeGeo',
        '_container','_onMouseMove','_onResize','_resizeObserver',
      ]
      keys.forEach(k => { this[k] = null })
    },
  },
}
</script>

<style scoped>
.particle-bg {
  position: absolute; inset: 0;
  pointer-events: none; z-index: 0; overflow: hidden;
}
.particle-bg >>> canvas { display: block; }
</style>
