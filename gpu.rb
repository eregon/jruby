# ary = Array.new(10) { |i| i }

# ary2 = Array.new(10) { |i| i+1 }

# r = ary.map { |e| e * 2 }

# p r

# c = 2
# p ary.zip(ary2).map { |a,b| a+b + c }

ary = [1.0, 2.0, 3.0]
r = nil

10000.times do
  r = ary.map_gpu { |e| e*e }
end
p r
