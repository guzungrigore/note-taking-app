import express from 'express';
import cors from 'cors'
const port = 5000;

const app = express();
app.use(express.json())
app.use(cors())
const cacheStore = new Map()

app.post('/cache', (req, res) => {
  const { userId, token } = req.body

  if (!userId || !token) {
    return res.status(400).json({ message: 'User ID and token are required' })
  }

  cacheStore.set(userId, token)
  console.log(`Stored token for user: ${userId}`)
  res.status(200).json({ message: 'Token stored successfully' })
});

app.get('/cache/:userId', (req, res) => {
  const userId = req.params.userId

  if (!cacheStore.has(userId)) {
    return res.status(404).json({ message: 'Token not found for this user' })
  }

  const token = cacheStore.get(userId)
  res.status(200).json({ userId, token })
});

app.listen(port,'0.0.0.0', () => {
  console.log(`Service running on port ${port}`);
})
