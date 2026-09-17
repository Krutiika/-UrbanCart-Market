import { useEffect, useMemo, useRef, useState } from 'react'
import { BrowserRouter, NavLink, Route, Routes, useNavigate } from 'react-router-dom'
import {
  Activity, ArrowRight, Bell, Check, CreditCard, Heart, LayoutDashboard,
  PackageCheck, Plus, Search, ShieldCheck, ShoppingBag, ShoppingCart, Star,
  Truck, Users, Wallet, X,
} from 'lucide-react'
import './App.css'

const fallbackProducts = [
  { id: 'aero-wireless-pro', name: 'AeroWireless Pro', category: 'Audio', price: 2499, rating: 4.8, tag: 'Bestseller', accent: '#5b7cff', description: 'Adaptive noise cancellation with 40-hour battery life.', available: true },
  { id: 'urbanfit-watch', name: 'UrbanFit Watch', category: 'Wearables', price: 3999, rating: 4.9, tag: 'New', accent: '#00b894', description: 'Precision health tracking in an ultra-light titanium design.', available: true },
  { id: 'lumabook-14', name: 'LumaBook 14', category: 'Laptops', price: 75999, rating: 4.7, tag: 'Premium', accent: '#ff7b54', description: 'Ultra-portable performance built for creators.', available: true },
  { id: 'pixelnest-speaker', name: 'PixelNest Speaker', category: 'Home', price: 1699, rating: 4.6, tag: 'Top rated', accent: '#d36bba', description: 'Room-filling sound with seamless smart-home connectivity.', available: true },
]

const categories = ['All', 'Audio', 'Wearables', 'Laptops', 'Home']
const customerId = localStorage.getItem('urbancart-customer') || `customer-${crypto.randomUUID().slice(0, 8)}`
localStorage.setItem('urbancart-customer', customerId)

async function request(path, options = {}) {
  const response = await fetch(`/api${path}`, { ...options, headers: { 'Content-Type': 'application/json', ...(options.headers || {}) } })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.message || `Request failed (${response.status})`)
  return body.data ?? body
}

async function loadProducts() {
  const results = await Promise.allSettled(categories.slice(1).map((category) => request(`/products/category/${category}`)))
  const products = results.flatMap((result) => (result.status === 'fulfilled' && Array.isArray(result.value) ? result.value : []))
  return products.length ? products.map(normalizeProduct) : fallbackProducts
}

function normalizeProduct(product) {
  const fallback = fallbackProducts.find((item) => item.id === product.id) || fallbackProducts[0]
  return { ...fallback, ...product, price: Number(product.price || fallback.price), rating: Number(product.rating || fallback.rating), accent: fallback.accent }
}

function App() {
  const [products, setProducts] = useState(fallbackProducts)
  const [cart, setCart] = useState([])
  const [orders, setOrders] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('All')
  const [search, setSearch] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(true)
  const addingProductIds = useRef(new Set())

  useEffect(() => {
    Promise.allSettled([loadProducts(), request(`/cart/${customerId}`), request(`/orders/customer/${customerId}`)])
      .then(([productResult, cartResult, orderResult]) => {
        if (productResult.status === 'fulfilled') setProducts(productResult.value)
        if (cartResult.status === 'fulfilled') setCart(cartResult.value?.items || [])
        if (orderResult.status === 'fulfilled') setOrders(orderResult.value || [])
      })
      .finally(() => setLoading(false))
  }, [])

  const filteredProducts = useMemo(() => products.filter((product) => {
    const categoryMatch = selectedCategory === 'All' || product.category?.toLowerCase() === selectedCategory.toLowerCase()
    const searchMatch = !search || `${product.name} ${product.category}`.toLowerCase().includes(search.toLowerCase())
    return categoryMatch && searchMatch
  }), [products, search, selectedCategory])

  const cartWithProducts = useMemo(() => cart.map((item) => {
    const product = products.find((entry) => entry.id === item.productId || entry.id === item.id)
    return { ...product, ...item, id: product?.id || item.productId || item.id, name: product?.name || item.productName, price: product?.price || Number(item.unitPrice || 0), accent: product?.accent || '#5b7cff' }
  }), [cart, products])
  const cartCount = cartWithProducts.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
  const subtotal = cartWithProducts.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const shipping = subtotal > 5000 || subtotal === 0 ? 0 : 299
  const total = subtotal + shipping

  const showNotice = (message) => {
    setNotice(message)
    window.setTimeout(() => setNotice(''), 4000)
  }

  const addToCart = async (product) => {
    if (cart.some((item) => item.productId === product.id) || addingProductIds.current.has(product.id)) {
      showNotice(`${product.name} is already in your cart`)
      return
    }
    const nextItem = { productId: product.id, productName: product.name, quantity: 1, unitPrice: product.price }
    addingProductIds.current.add(product.id)
    try {
      const saved = await request(`/cart/${customerId}/items`, { method: 'POST', body: JSON.stringify(nextItem) })
      setCart(saved.items || [...cart, nextItem])
      showNotice(`${product.name} added to your cart`)
    } catch {
      setCart((current) => { const existing = current.find((item) => item.productId === product.id); return existing ? current.map((item) => item.productId === product.id ? { ...item, quantity: item.quantity + 1 } : item) : [...current, nextItem] })
      showNotice('Backend is offline. Cart saved locally for this session.')
    } finally {
      addingProductIds.current.delete(product.id)
    }
  }

  const updateCartQuantity = async (product, delta) => {
    const nextQuantity = Math.max(0, product.quantity + delta)
    setCart((current) => current.map((item) => item.productId === product.id ? { ...item, quantity: nextQuantity } : item).filter((item) => item.quantity > 0))
    if (nextQuantity > 0) {
      try { await request(`/cart/${customerId}/items`, { method: 'POST', body: JSON.stringify({ productId: product.id, productName: product.name, quantity: delta, unitPrice: product.price }) }) } catch { /* keep local state while services boot */ }
    } else {
      try { await request(`/cart/${customerId}/items/${product.id}`, { method: 'DELETE' }) } catch { /* keep local state while services boot */ }
    }
  }

  const checkout = async (details) => {
    const first = cartWithProducts[0]
    if (!first) return
    try {
      const order = await request('/orders', { method: 'POST', headers: { 'Idempotency-Key': `checkout-${Date.now()}` }, body: JSON.stringify({ customerId, productId: first.id, quantity: cartCount, amount: total, paymentMode: details.paymentMode, deliveryAddress: details.deliveryAddress }) })
      await request('/payments/process', { method: 'POST', body: JSON.stringify({ orderId: order.id, amount: total, paymentMode: details.paymentMode }) })
      await request(`/cart/${customerId}`, { method: 'DELETE' })
      setOrders((current) => [order, ...current])
      setCart([])
      showNotice(`Order ${order.id} confirmed. Payment was successful.`)
    } catch (error) { showNotice(error.message || 'Checkout failed. Start the Java services and try again.') }
  }

  return <BrowserRouter><div className="app-shell">
    <header className="topbar"><NavLink to="/" className="brand-wrap"><div className="brand-mark">U</div><div><p className="eyebrow">UrbanCart</p><h2>Market</h2></div></NavLink><nav className="nav" aria-label="Main navigation"><NavLink to="/" className="nav-link">Shop</NavLink><NavLink to="/cart" className="nav-link">Cart</NavLink><NavLink to="/orders" className="nav-link">Orders</NavLink><NavLink to="/admin" className="nav-link">Operations</NavLink></nav><div className="topbar-actions"><label className="header-search"><Search size={16} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search products" /></label><button type="button" className="icon-button" aria-label="Notifications"><Bell size={18} /></button><NavLink to="/cart" className="cart-pill"><ShoppingCart size={17} /><span>{cartCount}</span></NavLink></div></header>
    {notice && <div className="toast" role="status"><Check size={17} /> {notice}<button type="button" onClick={() => setNotice('')}><X size={15} /></button></div>}
    {loading && <div className="sync-line"><Activity size={14} /> Syncing catalog, cart and order history...</div>}
    <Routes><Route path="/" element={<HomeScreen products={filteredProducts} categories={categories} selectedCategory={selectedCategory} setSelectedCategory={setSelectedCategory} addToCart={addToCart} />} /><Route path="/cart" element={<CartScreen cartWithProducts={cartWithProducts} subtotal={subtotal} shipping={shipping} total={total} updateCartQuantity={updateCartQuantity} checkout={checkout} />} /><Route path="/orders" element={<OrdersScreen orders={orders} />} /><Route path="/admin" element={<AdminScreen orders={orders} products={products} />} /></Routes>
  </div></BrowserRouter>
}

function HomeScreen({ products, categories, selectedCategory, setSelectedCategory, addToCart }) {
  const navigate = useNavigate()
  return <main className="page home-page"><section className="hero-panel"><div className="hero-copy"><span className="badge">Connected commerce</span><h1>Smart tech, curated for everyday life.</h1><p>Discover premium devices and essentials, then move from discovery to delivery in one focused flow.</p><div className="hero-actions"><button type="button" className="primary-btn" onClick={() => document.getElementById('catalog')?.scrollIntoView({ behavior: 'smooth' })}>Shop the catalog <ArrowRight size={16} /></button><button type="button" className="secondary-btn" onClick={() => navigate('/orders')}>Track an order</button></div><div className="mini-stats"><div className="mini-stat-card"><span>Catalog</span><strong>Live</strong></div><div className="mini-stat-card"><span>Delivery</span><strong>2 days</strong></div><div className="mini-stat-card"><span>Payments</span><strong>Secure</strong></div></div></div><div className="hero-visual"><div className="visual-card large-card"><div className="visual-glow" /><div className="device device-watch"><div className="watch-face"><span>12:45</span><small>URBAN / FIT</small></div></div></div><div className="visual-card small-card"><span className="small-label">Dispatch pulse</span><strong>96.4%</strong><Truck size={28} /></div></div></section><section id="catalog" className="section-header"><div><p className="eyebrow secondary">Live catalog</p><h3>Find your next favorite</h3></div><div className="filter-row">{categories.map((category) => <button key={category} type="button" className={selectedCategory === category ? 'filter active' : 'filter'} onClick={() => setSelectedCategory(category)}>{category}</button>)}</div></section><section className="product-grid">{products.map((product) => <ProductCard key={product.id} product={product} addToCart={addToCart} />)}</section>{!products.length && <div className="empty-state"><ShoppingBag size={24} /><h3>No products found</h3><p>Try a different category or search term.</p></div>}</main>
}

function ProductCard({ product, addToCart }) {
  return <article className="product-card"><div className="product-topbar"><span className="product-tag" style={{ background: `${product.accent}22`, color: product.accent }}>{product.tag || 'Available'}</span><button type="button" className="like-btn" aria-label={`Save ${product.name}`}><Heart size={16} /></button></div><div className="product-visual" style={{ background: `${product.accent}18` }}><div className="product-shape" style={{ background: product.accent }} /></div><div className="product-meta"><div className="meta-row"><span>{product.category}</span><span className="rating"><Star size={14} fill="currentColor" /> {product.rating}</span></div><h4>{product.name}</h4><p>{product.description || 'Designed for a more capable everyday.'}</p></div><div className="product-footer"><strong>₹{product.price.toLocaleString('en-IN')}</strong><button type="button" className="primary-btn narrow" onClick={() => addToCart(product)}><Plus size={15} /> Add</button></div></article>
}

function CartScreen({ cartWithProducts, subtotal, shipping, total, updateCartQuantity, checkout }) {
  const [isCheckoutOpen, setCheckoutOpen] = useState(false)
  return <main className="page cart-page"><section className="content-panel"><div className="panel-head"><div><p className="eyebrow secondary">Your bag</p><h3>Shopping cart</h3></div><span className="chip">{cartWithProducts.length} products</span></div>{cartWithProducts.length ? <div className="cart-layout"><div className="cart-list">{cartWithProducts.map((product) => <div key={product.id} className="cart-item"><div className="item-visual" style={{ background: `${product.accent}18` }}><div className="product-shape small" style={{ background: product.accent }} /></div><div className="item-copy"><h4>{product.name}</h4><p>{product.category}</p><strong>₹{product.price.toLocaleString('en-IN')}</strong></div><div className="qty-box"><button type="button" onClick={() => updateCartQuantity(product, -1)}>-</button><span>{product.quantity}</span><button type="button" onClick={() => updateCartQuantity(product, 1)}>+</button></div></div>)}</div><aside className="checkout-box"><h4>Order summary</h4><div className="summary-row"><span>Subtotal</span><strong>₹{subtotal.toLocaleString('en-IN')}</strong></div><div className="summary-row"><span>Shipping</span><strong>{shipping ? `₹${shipping}` : 'Free'}</strong></div><div className="summary-row total-row"><span>Total</span><strong>₹{total.toLocaleString('en-IN')}</strong></div><button type="button" className="primary-btn full-width" onClick={() => setCheckoutOpen(true)}><CreditCard size={16} /> Proceed to checkout</button></aside></div> : <div className="empty-state"><ShoppingCart size={26} /><h3>Your cart is waiting</h3><p>Add something from the live catalog to begin.</p><NavLink to="/" className="primary-btn">Browse products <ArrowRight size={16} /></NavLink></div>}</section>{isCheckoutOpen && <CheckoutModal total={total} onClose={() => setCheckoutOpen(false)} onSubmit={checkout} />}</main>
}

function CheckoutModal({ total, onClose, onSubmit }) {
  const [address, setAddress] = useState('')
  const [paymentMode, setPaymentMode] = useState('CARD')
  return <div className="modal-backdrop"><form className="checkout-modal" onSubmit={(event) => { event.preventDefault(); onSubmit({ deliveryAddress: address, paymentMode }); onClose() }}><div className="modal-head"><div><p className="eyebrow secondary">Final step</p><h3>Complete your order</h3></div><button type="button" className="icon-button" onClick={onClose} aria-label="Close checkout"><X size={18} /></button></div><label>Delivery address<input required value={address} onChange={(event) => setAddress(event.target.value)} placeholder="Apartment, street, city" /></label><label>Payment method<select value={paymentMode} onChange={(event) => setPaymentMode(event.target.value)}><option value="CARD">Card</option><option value="UPI">UPI</option><option value="COD">Cash on delivery</option></select></label><div className="modal-total"><span>Payable now</span><strong>₹{total.toLocaleString('en-IN')}</strong></div><button type="submit" className="primary-btn full-width"><ShieldCheck size={16} /> Confirm and pay</button></form></div>
}

function OrdersScreen({ orders }) {
  return <main className="page orders-page"><section className="content-panel"><div className="panel-head"><div><p className="eyebrow secondary">Lifecycle</p><h3>Recent orders</h3></div><span className="chip">{orders.length} synced</span></div>{orders.length ? <div className="table-wrap"><table><thead><tr><th>Order ID</th><th>Customer</th><th>Status</th><th>Amount</th><th>Payment</th></tr></thead><tbody>{orders.map((order) => <tr key={order.id}><td>{order.id}</td><td>{order.customerId || 'You'}</td><td><span className="status-pill confirmed">{order.orderStatus || 'CONFIRMED'}</span></td><td>₹{Number(order.amount || 0).toLocaleString('en-IN')}</td><td>{order.paymentStatus || 'PAYMENT_PENDING'}</td></tr>)}</tbody></table></div> : <div className="empty-state"><PackageCheck size={26} /><h3>No orders yet</h3><p>Your completed checkouts will appear here.</p></div>}</section></main>
}

function AdminScreen({ orders, products }) {
  const [healthy, setHealthy] = useState(null)
  useEffect(() => { request('/notifications/health-check').then(() => setHealthy(true)).catch(() => setHealthy(false)) }, [])
  const metrics = [{ label: 'Revenue', value: `₹${orders.reduce((sum, order) => sum + Number(order.amount || 0), 0).toLocaleString('en-IN')}`, change: 'Live', icon: Wallet }, { label: 'Orders', value: orders.length, change: 'Synced', icon: PackageCheck }, { label: 'Catalog', value: products.length, change: 'Loaded', icon: Users }, { label: 'Fulfillment', value: '96.4%', change: 'Healthy', icon: Truck }]
  return <main className="page admin-page"><section className="metrics-grid">{metrics.map(({ label, value, change, icon: Icon }) => <article key={label} className="metric-card"><div className="metric-icon"><Icon size={18} /></div><div><span>{label}</span><strong>{value}</strong></div><em>{change}</em></article>)}</section><section className="admin-grid"><div className="content-panel"><div className="panel-head compact"><div><p className="eyebrow secondary">Operations</p><h3>Service health</h3></div><Activity size={18} color="#21c77a" /></div><ul className="health-list"><li><span className="dot green" /> MySQL-backed services connected</li><li><span className={healthy === false ? 'dot amber' : 'dot green'} /> Notification service {healthy === false ? 'offline' : 'operational'}</li><li><span className="dot blue" /> Gateway routes ready on port 8080</li></ul></div><div className="content-panel"><div className="panel-head compact"><div><p className="eyebrow secondary">Commerce pulse</p><h3>Fulfillment overview</h3></div><LayoutDashboard size={18} /></div><div className="bars-panel">{[58, 72, 64, 86, 75, 92].map((bar, index) => <div key={bar + index} className="bar-row"><span>W{index + 1}</span><div className="bar-track"><div className="bar-fill" style={{ width: `${bar}%` }} /></div><strong>{bar}%</strong></div>)}</div></div></section></main>
}

export default App
