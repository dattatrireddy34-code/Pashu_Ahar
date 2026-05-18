const express = require('express');
const { getCostSummary, addTransaction } = require('../controllers/costController');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(protect);

router.get('/summary', getCostSummary);
router.post('/transaction', addTransaction);

module.exports = router;
