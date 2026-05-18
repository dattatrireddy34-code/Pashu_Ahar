const express = require('express');
const { getBreeds, seedBreeds } = require('../controllers/breedController');

const router = express.Router();

router.get('/', getBreeds);
router.post('/seed', seedBreeds);

module.exports = router;
