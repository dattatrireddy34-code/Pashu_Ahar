const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const connectDB = require('./config/db');

// Load env vars
dotenv.config();

// Connect to database
connectDB();

const app = express();

// Body parser
app.use(express.json());

// Enable CORS
app.use(cors());

// Route files
const auth = require('./routes/authRoutes');
const cows = require('./routes/cowRoutes');
const users = require('./routes/userRoutes');
const ingredients = require('./routes/ingredientRoutes');
const recipes = require('./routes/recipeRoutes');
const diseases = require('./routes/diseaseRoutes');
const tips = require('./routes/tipRoutes');
const breeds = require('./routes/breedRoutes');
const costs = require('./routes/costRoutes');

// Mount routers
app.use('/api/auth', auth);
app.use('/api/cows', cows);
app.use('/api/users', users);
app.use('/api/ingredients', ingredients);
app.use('/api/recipes', recipes);
app.use('/api/diseases', diseases);
app.use('/api/tips', tips);
app.use('/api/breeds', breeds);
app.use('/api/costs', costs);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server running in ${process.env.NODE_ENV || 'development'} mode on port ${PORT}`);
});
