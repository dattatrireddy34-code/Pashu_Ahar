const express = require('express');
const { getTips, seedTips } = require('../controllers/tipController');

const router = express.Router();

router.get('/', getTips);
router.post('/seed', seedTips);

module.exports = router;
