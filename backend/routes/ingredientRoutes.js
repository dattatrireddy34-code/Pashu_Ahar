const express = require('express');
const { getIngredients, upsertIngredient, seedIngredients } = require('../controllers/ingredientController');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(protect);

router.get('/', getIngredients);
router.post('/', upsertIngredient);
router.post('/seed', seedIngredients);

module.exports = router;
