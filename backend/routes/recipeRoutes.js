const express = require('express');
const { getRecipe } = require('../controllers/recipeController');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.get('/:cowId', protect, getRecipe);

module.exports = router;
