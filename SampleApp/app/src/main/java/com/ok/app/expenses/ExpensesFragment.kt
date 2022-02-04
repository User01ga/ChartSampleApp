package com.ok.app.expenses

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ok.app.R
import com.ok.app.databinding.FragmentExpensesBinding
import com.ok.chart.AngleAnimation
import com.ok.chart.ProgressAnimation
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Olga Kuzmina.
 */
@AndroidEntryPoint
class ExpensesFragment : Fragment() {

    private val viewModel: ExpensesViewModel by viewModels()

    private lateinit var binding: FragmentExpensesBinding

    private lateinit var adapter: TransactionAdapter

    private var isExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExpensesBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter()
        binding.list.adapter = adapter
        binding.amountHolder.setOnClickListener { expandChart() }

        showExpenses()
        showTransactions()

        loadExpenses()
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModel.loadTransactions()
    }

    private fun loadExpenses() {
        viewModel.loadExpenses()
    }

    private fun showTransactions() {
        viewModel.transactions.observe(viewLifecycleOwner, { items -> adapter.setList(items) })
    }

    private fun showExpenses() {
        viewModel.expensesAmount.observe(viewLifecycleOwner, { amount ->
            binding.amount.text = "- %.2f".format(amount) // TODO format with BigDecimal
        })
        viewModel.expensesByCategory.observe(viewLifecycleOwner, { categoryWithAmount ->
            val values = mutableListOf<Float>()
            val colors = mutableListOf<Int>()
            categoryWithAmount.forEach { item ->
                values.add(item.total.toFloat())
                item.category.categoryColor(requireContext())?.let { colors.add(it) }
            }
            binding.lineChart.setItems(values, colors)
            binding.pieChart.setItems(values, colors)
            showLineChart()
        })
    }

    private fun showLineChart() {
        val lineAnimation = ProgressAnimation(binding.lineChart).apply {
            interpolator = AccelerateInterpolator()
            duration = 500
        }
        binding.lineChart.startAnimation(lineAnimation)
    }

    private fun showPieChart() {
        val pieAnimation = AngleAnimation(binding.pieChart).apply {
            interpolator = AccelerateInterpolator()
            duration = 500
        }
        binding.pieChart.startAnimation(pieAnimation)
    }

    private fun expandChart() {
        val from: Int
        val to: Int
        val opaque: Float
        val degree: Float
        val scale: Float
        if (isExpanded) {
            isExpanded = false
            from = binding.chartHolder.measuredHeight
            to = binding.lineChart.measuredHeight + binding.title.measuredHeight + resources.getDimensionPixelSize(R.dimen.double_space) * 2
            opaque = 1F
            degree = 0F
            scale = 0F
        } else {
            isExpanded = true
            from = binding.chartHolder.measuredHeight
            to = binding.chartHolder.measuredHeight +
                    resources.getDimensionPixelSize(R.dimen.pie_chart_size) + resources.getDimensionPixelSize(R.dimen.double_space) * 2
            opaque = 0F
            degree = 180F
            scale = 1F
            binding.pieChart.visibility = View.VISIBLE
            showPieChart()
        }

        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { valueAnimator ->
            val height = valueAnimator.animatedValue as Int
            val layoutParams: ConstraintLayout.LayoutParams = binding.chartHolder.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = height
            binding.chartHolder.layoutParams = layoutParams
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                if (!isExpanded) binding.pieChart.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        anim.duration = 500
        anim.start()

        binding.pieChart.animate().scaleX(scale).scaleY(scale)
        binding.lineChart.animate().alpha(opaque)
        binding.arrow.animate().rotation(degree)
    }
}