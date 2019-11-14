package com.netzwelt.tutormodule.ui.dashboard.classmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.netzwelt.tutormodule.R
import com.netzwelt.tutormodule.databinding.ItemTutorManageClassesBinding

class ClassesAdaptor(var context: Context)
    : RecyclerView.Adapter<ClassesAdaptor.Holder>() {

    private var userList: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val exploreRowBinding: ItemTutorManageClassesBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_tutor_manage_classes, parent, false
                )

        return Holder(exploreRowBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        /*  holder.exploreRowBinding.setItemClickListener(mItemClickListener)
          userList[position].let {
              with(holder.exploreRowBinding)
              {

                  if (mCurrentLocation != null) {
                      txtdistance.setText("${it.distance.toString()} Km")
                  }

                  user = it
                  tutorname.setText(it.firstName + " " + it.lastName)
                  txtclassprice.setText(it.rate + " / " + it.noOfClasses + " PER " + it.paymentDuration)


                  var splitarray = it.classesToTeach.split(",")
                  if (splitarray.size > 0) {
                      var stringBuilder = StringBuilder(splitarray.size)
                      for (i in splitarray.indices) {
                          if (i == splitarray.size - 1) {
                              stringBuilder.append(splitarray.get(i) + CommonUtils.getClassSuffix(splitarray.get(i).toInt()))

                          } else {
                              stringBuilder.append(splitarray.get(i) + CommonUtils.getClassSuffix(splitarray.get(i).toInt()) + " - ")
                          }
                      }
                      txtgrades.setText(stringBuilder.toString())


                  } else {
                      txtgrades.setText(it.classesToTeach)

                  }



                  txtclasstype.setText(it.classType)
                  txtsubjects.setText(it.subjectsToTeach.replace(",", " | "))

                  if (!TextUtils.isEmpty(it.pictureUrl)) {
                      val pathReference1 = FirebaseStorage.getInstance().reference.child(it.pictureUrl)
                      val options = RequestOptions()
                      options.transforms(MultiTransformation(CenterCrop(), RoundedCornersTransformation(context, CommonUtils.convertDpToPixel(6f, context).toInt(), 0)))
                      options.placeholder(R.drawable.dummyexploreimage)
                      Glide.with(context).load(pathReference1)
                              .apply(options).into(imgprofile)

                  }

              }
          }*/

    }

    override fun getItemCount(): Int {
        return 2/*userList.size*/
    }

    fun setData(userList: List<String>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    inner class Holder(exploreRowBinding: ItemTutorManageClassesBinding) :
            RecyclerView.ViewHolder(exploreRowBinding.root) {
        fun bind() {
        }
    }


}